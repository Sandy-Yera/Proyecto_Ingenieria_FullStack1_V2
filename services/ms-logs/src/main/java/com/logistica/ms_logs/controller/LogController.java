package com.logistica.ms_logs.controller;

import com.logistica.ms_logs.dto.LogResponseDTO;
import com.logistica.ms_logs.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * 🔵 SOLUCIÓN MEDIO 3: Endpoint de Consulta Paginado y Filtrado.
     * Reemplaza la descarga masiva por consultas controladas y paginadas.
     * * Ejemplos de uso en Postman o Frontend:
     * - /api/logs?page=0&size=20
     * - /api/logs?level=ERROR
     * - /api/logs?serviceName=ms-auth&level=INFO&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<LogResponseDTO>> obtenerLogsPaginados(
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Page<LogResponseDTO> logsPaginados = logService.listarConPaginacion(serviceName, level, page, size);
        return ResponseEntity.ok(logsPaginados);
    }
}