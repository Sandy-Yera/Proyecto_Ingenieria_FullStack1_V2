package com.logistica.ms_logs.service;

import com.logistica.ms_logs.dto.LogResponseDTO;
import com.logistica.ms_logs.model.LogEntity;
import com.logistica.ms_logs.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // Listar todos los logs del sistema
    public List<LogResponseDTO> listarTodos() {
        return logRepository.findAll().stream()
                .map(this::convertirADto)
                .toList();
    }

    // Helper para conversión manual sin librerías extra
    private LogResponseDTO convertirADto(LogEntity entity) {
        LogResponseDTO dto = new LogResponseDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setLevel(entity.getLevel());
        dto.setMessage(entity.getMessage());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}