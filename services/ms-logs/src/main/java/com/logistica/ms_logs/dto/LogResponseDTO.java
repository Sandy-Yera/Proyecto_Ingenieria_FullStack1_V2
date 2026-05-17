package com.logistica.ms_logs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogResponseDTO {
    private Long id;
    private String serviceName;
    private String level;
    private String message;
    private String timestamp;
}