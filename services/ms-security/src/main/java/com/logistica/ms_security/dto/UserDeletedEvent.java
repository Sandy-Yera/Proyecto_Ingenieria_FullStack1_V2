package com.logistica.ms_security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para deserializar el evento asíncrono de eliminación de usuarios desde Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent {
    
    private Long userId;
    private String traceId;
}