package com.logistica.ms_logs.service;

import com.logistica.ms_logs.dto.LogResponseDTO;
import com.logistica.ms_logs.model.LogEntity;
import com.logistica.ms_logs.repository.LogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public Page<LogResponseDTO> listarConPaginacion(String serviceName, String level, int pagina, int tamano) {
        String filtroServicio = (serviceName != null) ? serviceName.trim() : "";
        String filtroNivel = (level != null) ? level.trim() : "";

        // Ordenamos por ID de forma descendente para ver lo más nuevo primero
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("id").descending());

        // 3. Ejecutar la consulta optimizada en la base de datos y mapear los resultados de forma reactiva/funcional
        return logRepository.findByServiceNameContainingAndLevelContaining(filtroServicio, filtroNivel, pageable)
                .map(this::convertirADto);
    }

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