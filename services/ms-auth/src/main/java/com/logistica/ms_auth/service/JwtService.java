package com.logistica.ms_auth.service;

import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.repository.UserCredencialRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // Blacklist en memoria — Set thread-safe para tokens revocados activamente
    // Para escala real, reemplazar por Redis con TTL = expirationMs
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of(
                        "username", username,
                        "userId", userId
                ))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token) {
        if (tokenBlacklist.contains(token)) {
            throw new JwtException("Token revocado — usuario eliminado o sesión invalidada.");
        }
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // añadir validación con consulta a DB
    public boolean isTokenValidAndUserActive(String token, UserCredencialRepository repo) {
        try {
            Claims claims = validateToken(token); // chequea firma y expiración y blacklist
            Long userId = Long.valueOf(claims.getSubject());
            return repo.findById(userId)
                    .map(UserCredencial::getIsActive)
                    .orElse(false); // si no existe, el token es inválido
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Añade el token a la blacklist en memoria.
     * Se invoca desde el consumer Kafka cuando se recibe user-deleted-topic.
     */
    public void revokeToken(String token) {
        tokenBlacklist.add(token);
    }

    public Long extractUserId(String token) {
        return Long.valueOf(validateToken(token).getSubject());
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
