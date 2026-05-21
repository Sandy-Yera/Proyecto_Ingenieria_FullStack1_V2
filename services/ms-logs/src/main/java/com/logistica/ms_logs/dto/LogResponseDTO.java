package com.logistica.ms_logs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogResponseDTO {
    private Long id;
    private String serviceName;
    private String level;
    private String message;
    private Instant timestamp; //modificado a Instant
}