package com.logistica.ms_auth.service;

import com.logistica.ms_auth.dto.LoginRequestDTO;
import com.logistica.ms_auth.dto.LoginResponseDTO;
import com.logistica.ms_auth.exception.entity.EntityNotFoundException;
import com.logistica.ms_auth.exception.entity.EntityBadRequestException;
import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.repository.UserCredencialRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredencialRepository userCredencialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaLogProducer logProducer;
    private final HttpServletRequest request;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String traceId = request.getHeader("X-Trace-Id");

        // Buscar por username (correo)
        UserCredencial credencial = userCredencialRepository
                .findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Intento de login fallido. Username no existe: "
                            + dto.getUsername() + " | TraceId: " + traceId);
                    return new EntityNotFoundException("Credenciales inválidas.");
                });

        // Verificar cuenta activa
        if (!credencial.getIsActive()) {
            logProducer.sendLog("WARN", "Intento de login sobre cuenta desactivada ID: "
                    + credencial.getId() + " | TraceId: " + traceId);
            throw new EntityBadRequestException("La cuenta está desactivada.");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(dto.getPassword(), credencial.getPassword())) {
            logProducer.sendLog("WARN", "Contraseña incorrecta para username: "
                    + dto.getUsername() + " | TraceId: " + traceId);
            throw new EntityBadRequestException("Credenciales inválidas.");
        }

        // Actualizar último login
        credencial.setLastLogin(LocalDateTime.now());

        String token = jwtService.generateToken(credencial.getId(), credencial.getUsername());

        logProducer.sendLog("INFO", "Login exitoso para usuario ID: "
                + credencial.getId() + " | TraceId: " + traceId);

        return new LoginResponseDTO(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                credencial.getId(),
                credencial.getUsername());
    }

    // Endpoint de validación que el API Gateway puede consultar
    public boolean validateToken(String token) {
        return jwtService.isTokenValidAndUserActive(token, userCredencialRepository);
    }
}