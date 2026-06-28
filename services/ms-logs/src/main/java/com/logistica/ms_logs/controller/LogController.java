package com.logistica.ms_logs.controller;

import com.logistica.ms_logs.dto.LogResponseDTO;
import com.logistica.ms_logs.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Logs", description = "Consulta centralizada de logs del sistema BRM")
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
    @Operation(summary = "Listar logs paginados", description = "Consulta logs centralizados con filtros opcionales por servicio y nivel.")
    @ApiResponse(responseCode = "200", description = "Página de logs retornada")
    @GetMapping
    public ResponseEntity<Page<LogResponseDTO>> obtenerLogsPaginados(
            @Parameter(description = "Nombre del microservicio origen") @RequestParam(required = false) String serviceName,
            @Parameter(description = "Nivel del log (INFO, WARN, ERROR)") @RequestParam(required = false) String level,
            @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "50") int size
    ) {
        Page<LogResponseDTO> logsPaginados = logService.listarConPaginacion(serviceName, level, page, size);
        return ResponseEntity.ok(logsPaginados);
    }
}