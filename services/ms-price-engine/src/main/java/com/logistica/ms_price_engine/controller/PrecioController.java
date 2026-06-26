package com.logistica.ms_price_engine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_price_engine.dto.PrecioRequestDTO;
import com.logistica.ms_price_engine.dto.PrecioResponseDTO;
import com.logistica.ms_price_engine.service.PrecioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/precios")
@RequiredArgsConstructor
@Tag(name = "Precios", description = "Gestión de precios de reparación del sistema BRM")
public class PrecioController {
    private final PrecioService precioService;

    @Operation(summary = "Calcular el precio de una reparación", description = "Calcula el precio total de una reparación basado en la categoría, horas de trabajo y unidades de material.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Precio calculado exitosamente", content = @Content(schema = @Schema(implementation = PrecioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos", content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontró una tarifa para la categoría especificada", content = @Content)
    })
    @PostMapping("/calcular")
    public ResponseEntity<PrecioResponseDTO> calcularPrecio(@Valid @RequestBody PrecioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(precioService.calcularPrecio(dto));
    }
}
