package com.logistica.ms_auth.dto;

import java.time.LocalDateTime;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UserCredencialResponseDTO {
    
    private Long id;
    private String username;
    
    // Forzamos a Jackson a mantener el nombre exacto "isActive" en el JSON, 
    // evitando que Lombok lo mutile a "active" al generar los bytes de red.
    @JsonProperty("isActive")
    private Boolean isActive;
    
    // Migramos a la API de tiempo moderna de Java para evitar problemas 
    // de deserialización entre microservicios.
    private LocalDateTime lastLogin;
}