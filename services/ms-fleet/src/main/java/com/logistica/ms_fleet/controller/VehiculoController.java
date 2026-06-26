package com.logistica.ms_fleet.controller;

import java.util.List;
import java.util.Map;

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

import com.logistica.ms_fleet.dto.GpsLocationDTO;
import com.logistica.ms_fleet.dto.VehiculoRequestDTO;
import com.logistica.ms_fleet.dto.VehiculoResponseDTO;
import com.logistica.ms_fleet.service.VehiculoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fleet")
@RequiredArgsConstructor
@Tag(name = "Flota", description = "Gestión de vehículos y telemetría GPS en tiempo real del sistema BRM")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @Operation(summary = "Registrar vehículo", description = "Registra un nuevo vehículo en la flota.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehículo registrado",
            content = @Content(schema = @Schema(implementation = VehiculoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Placa duplicada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> crearVehiculo(@Valid @RequestBody VehiculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crearVehiculo(dto));
    }

    @Operation(summary = "Listar vehículos", description = "Retorna todos los vehículos de la flota.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin vehículos registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listarVehiculos() {
        List<VehiculoResponseDTO> lista = vehiculoService.listarVehiculos();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener vehículo por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehículo encontrado",
            content = @Content(schema = @Schema(implementation = VehiculoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> obtenerVehiculoPorId(
            @Parameter(description = "ID del vehículo") @PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.obtenerVehiculoPorId(id));
    }

    @Operation(summary = "Actualizar vehículo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehículo actualizado",
            content = @Content(schema = @Schema(implementation = VehiculoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Placa duplicada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizarVehiculo(
            @PathVariable Long id, @Valid @RequestBody VehiculoRequestDTO dto) {
        return ResponseEntity.ok(vehiculoService.actualizarVehiculo(id, dto));
    }

    @Operation(summary = "Eliminar vehículo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Vehículo eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {
        vehiculoService.eliminarVehiculo(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Registrar ubicación GPS",
        description = "Publica la telemetría GPS del vehículo al topic Kafka 'fleet-gps-tracking'."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ubicación publicada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos GPS inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado", content = @Content)
    })
    @PostMapping("/{id}/location")
    public ResponseEntity<Map<String, String>> registrarUbicacion(
            @Parameter(description = "ID del vehículo") @PathVariable Long id,
            @Valid @RequestBody GpsLocationDTO dto) {
        vehiculoService.registrarUbicacion(id, dto);
        return ResponseEntity.ok(Map.of(
                "message", "Ubicación GPS publicada correctamente para vehículo ID: " + id));
    }
}
