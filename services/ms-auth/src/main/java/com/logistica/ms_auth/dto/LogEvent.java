package com.logistica.ms_auth.dto;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE EVENTO DE LOG PARA MENSAJERÍA ASÍNCRONA (KAFKA) - MS-AUTH
 * REFACTORIZACIÓN: Sincronizado a Instant con formato ISO-8601 para mantener 
 * simetría absoluta con ms-users en el tópico centralizado.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent {
    
    private String serviceName; // Recibirá "ms-auth" de forma nativa
    private String level;       // INFO, WARN, ERROR
    private String message;
    
    // Formato universal ISO-8601 UTC para indexación limpia de logs
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant timestamp;  
}