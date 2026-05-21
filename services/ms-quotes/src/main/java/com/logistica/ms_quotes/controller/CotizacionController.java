package com.logistica.ms_quotes.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_quotes.dto.CotizacionRequestDTO;  // 🟢 Nuevo: Import Request DTO
import com.logistica.ms_quotes.dto.CotizacionResponseDTO; // 🟢 Nuevo: Import Response DTO
import com.logistica.ms_quotes.service.CotizacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;

    // CREAR
    @PostMapping
    public ResponseEntity<CotizacionResponseDTO> crearCotizacion(@Valid @RequestBody CotizacionRequestDTO cotizacionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.crearCotizacion(cotizacionDTO));
    }

    // LEER
    @GetMapping
    public ResponseEntity<List<CotizacionResponseDTO>> listarCotizaciones() {
        List<CotizacionResponseDTO> listado = cotizacionService.listarCotizaciones();

        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> actualizarCotizacion(
            @Valid @RequestBody CotizacionRequestDTO datosActualizados,
            @PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.actualizarCotizacion(id, datosActualizados));
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCotizacion(@PathVariable Long id) {
        cotizacionService.eliminarCotizacion(id);
        return ResponseEntity.noContent().build();
    }
}