package com.logistica.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping; // Agregado para el borrado compensatorio
import org.springframework.web.bind.annotation.PathVariable;
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
     * Sincronizado milimétricamente con el @PutMapping del modulo de seguridad.
     * Envía las nuevas credenciales de correo a ms-auth asociadas al ID del usuario.
     */
    @PutMapping("/api/auth/usuario/{userId}")
    ResponseEntity<UserCredencialResponseDTO> actualizarCredencialesRemotas(
            @PathVariable("userId") Long userId, 
            @RequestBody UserCredencialRegisterDTO dto
    );

    /**
     * 🟠 NUEVO MÉTODO (Solución Alto 1 - Saga Compensatoria):
     * Mapea directamente a DELETE /api/auth/{id} en ms-auth.
     * Se invoca si el flujo distributivo de creación se rompe, eliminando credenciales huérfanas.
     */
    @DeleteMapping("/api/auth/{id}")
    ResponseEntity<Void> eliminarCredencialesRemotas(@PathVariable("id") Long id);
}