package com.logistica.ms_security.dto;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE EVENTO DE LOG PARA MENSAJERÍA ASÍNCRONA (KAFKA)
 * OPTIMIZACIÓN: Se migró el timestamp de String a Instant con formato ISO-8601 
 * para garantizar indexación nativa en sistemas de monitoreo (Elasticsearch/Splunk).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent {
    
    private String serviceName; // Nombre del microservicio emisor (ms-security)
    private String level;       // INFO, WARN, ERROR
    private String message;     // Detalle técnico del evento o traza
    
    // Forzamos a que el formato en el JSON final sea el estándar de la industria Z (UTC)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant timestamp;  // Momento exacto del evento en el tiempo
}