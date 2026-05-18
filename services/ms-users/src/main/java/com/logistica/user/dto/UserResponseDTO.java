package com.logistica.user.dto;

import lombok.Data;

/**
 * DTO DE RESPUESTA DE USUARIO (ms-users)
 * OPTIMIZACIÓN: Se unifican los campos 'rut' y 'dv' en un único String 'rut' 
 * para cumplir con el informe de "RUT bruto completo" y asegurar compatibilidad 
 * simétrica con ms-auth.
 */
@Data
public class UserResponseDTO {
    private Long id;
    private String rut; // Encapsula el RUT completo bruto (ej: "123456789" o "12345678-9")
    private String pNombre;
    private String sNombre;
    private String apPat;
    private String apMat;
    
    // CORREGIDO: Cambiado de Integer a String para mantener simetría total con la entidad y el RegisterDTO
    private String telefono; 
    
    private String correo;
}