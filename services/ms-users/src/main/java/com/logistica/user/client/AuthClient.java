package com.logistica.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; // Agregado para la actualización
import org.springframework.web.bind.annotation.PathVariable; // Agregado para capturar el ID
import org.springframework.web.bind.annotation.RequestBody;

import com.logistica.user.dto.UserCredencialResponseDTO;
import com.logistica.user.dto.UserCredencialRegisterDTO;

/**
 * CONTRATO DE COMUNICACIÓN SÍNCRONA (OpenFeign)
 * Este archivo VIVE en ms-users, pero apunta de manera lógica
 * al microservicio remoto "ms-auth" registrado en Eureka.
 */
@FeignClient(name = "ms-auth")
public interface AuthClient {

    /**
     * Sincronizado milimétricamente con el @PostMapping raíz 
     * del UserCredencialController de ms-auth (/api/auth).
     */
    @PostMapping("/api/auth") 
    ResponseEntity<UserCredencialResponseDTO> generarCredencialesRemotas(@RequestBody UserCredencialRegisterDTO dto);

    /**
     * 🟢 NUEVO MÉTODO (Solución Crítico 1):
     * Envía las nuevas credenciales de correo a ms-auth asociadas al ID del usuario.
     * Mapea directamente con el endpoint de actualización remota.
     */
    @PutMapping("/api/auth/usuario/{userId}")
    ResponseEntity<UserCredencialResponseDTO> actualizarCredencialesRemotas(
            @PathVariable("userId") Long userId, 
            @RequestBody UserCredencialRegisterDTO dto
    );
}