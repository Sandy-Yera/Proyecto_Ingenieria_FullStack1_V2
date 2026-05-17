package com.logistica.ms_logs.controller;

import com.logistica.ms_logs.dto.LogResponseDTO;
import com.logistica.ms_logs.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<LogResponseDTO>> obtenerTodosLosLogs() {
        List<LogResponseDTO> logs = logService.listarTodos();
        return ResponseEntity.ok(logs);
    }
}