package com.logistica.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO espejo para recibir la respuesta de la creación de credenciales desde ms-auth.
 * Mantiene consistencia con el ID compartido de tipo Long.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredencialResponseDTO {
    private Long id;            // ID compartido unificado
    private String username;    // Correo electrónico del usuario
    private String status;      // Estado de la operación (ej: "CREATED", "ACTIVE")
}