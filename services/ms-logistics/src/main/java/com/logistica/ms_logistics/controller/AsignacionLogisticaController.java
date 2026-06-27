package com.logistica.ms_logistics.controller;

import com.logistica.ms_logistics.dto.AsignacionRequestDTO;
import com.logistica.ms_logistics.dto.AsignacionResponseDTO;
import com.logistica.ms_logistics.dto.CambioEstadoRequestDTO;
import com.logistica.ms_logistics.dto.DistanciaResponseDTO;
import com.logistica.ms_logistics.model.EstadoLogistica;
import com.logistica.ms_logistics.service.AsignacionLogisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@Tag(name = "Logística", description = "Orquestación logística con cálculo de distancia Haversine")
public class AsignacionLogisticaController {

    private final AsignacionLogisticaService service;

    @Operation(summary = "Crear asignación logística", description = "Registra una nueva asignación en estado PLANIFICADO. La distancia y tiempo estimado se calculan automáticamente.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asignación creada",
            content = @Content(schema = @Schema(implementation = AsignacionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Orden no está en estado ASSIGNED", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden o vehículo no encontrados", content = @Content),
        @ApiResponse(responseCode = "409", description = "Ya existe una asignación activa para esa orden", content = @Content)
    })
    @PostMapping("/asignaciones")
    public ResponseEntity<AsignacionResponseDTO> crearAsignacion(@Valid @RequestBody AsignacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearAsignacion(dto));
    }

    @Operation(summary = "Listar todas las asignaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin asignaciones registradas", content = @Content)
    })
    @GetMapping("/asignaciones")
    public ResponseEntity<List<AsignacionResponseDTO>> listarTodas() {
        List<AsignacionResponseDTO> lista = service.listarTodas();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener asignación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignación encontrada",
            content = @Content(schema = @Schema(implementation = AsignacionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada", content = @Content)
    })
    @GetMapping("/asignaciones/{id}")
    public ResponseEntity<AsignacionResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la asignación") @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Listar asignaciones por orden de trabajo")
    @GetMapping("/asignaciones/workorder/{woId}")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorOrden(@PathVariable Long woId) {
        List<AsignacionResponseDTO> lista = service.listarPorOrden(woId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar asignaciones por vehículo")
    @GetMapping("/asignaciones/vehiculo/{vehiculoId}")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorVehiculo(@PathVariable Long vehiculoId) {
        List<AsignacionResponseDTO> lista = service.listarPorVehiculo(vehiculoId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar asignaciones por técnico")
    @GetMapping("/asignaciones/tecnico/{tecnicoId}")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorTecnico(@PathVariable Long tecnicoId) {
        List<AsignacionResponseDTO> lista = service.listarPorTecnico(tecnicoId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar asignaciones por estado")
    @GetMapping("/asignaciones/estado/{estado}")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorEstado(@PathVariable EstadoLogistica estado) {
        List<AsignacionResponseDTO> lista = service.listarPorEstado(estado);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Marcar en ruta", description = "Transición PLANIFICADO → EN_RUTA. Registra la fecha de salida real.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "En ruta",
            content = @Content(schema = @Schema(implementation = AsignacionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada", content = @Content)
    })
    @PutMapping("/asignaciones/{id}/en-ruta")
    public ResponseEntity<AsignacionResponseDTO> enRuta(@PathVariable Long id) {
        return ResponseEntity.ok(service.enRuta(id));
    }

    @Operation(summary = "Completar asignación", description = "Transición EN_RUTA → COMPLETADO. Registra la fecha de llegada real.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignación completada",
            content = @Content(schema = @Schema(implementation = AsignacionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada", content = @Content)
    })
    @PutMapping("/asignaciones/{id}/completar")
    public ResponseEntity<AsignacionResponseDTO> completar(
            @PathVariable Long id, @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(service.completar(id, dto));
    }

    @Operation(summary = "Cancelar asignación", description = "Transición a CANCELADO desde cualquier estado excepto COMPLETADO.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignación cancelada",
            content = @Content(schema = @Schema(implementation = AsignacionResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "No se puede cancelar una asignación COMPLETADO", content = @Content),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada", content = @Content)
    })
    @PutMapping("/asignaciones/{id}/cancelar")
    public ResponseEntity<AsignacionResponseDTO> cancelar(
            @PathVariable Long id, @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(service.cancelar(id, dto));
    }

    @Operation(summary = "Calcular distancia y tiempo estimado",
        description = "Endpoint utilitario: calcula distancia Haversine en km y tiempo estimado en minutos (velocidad urbana 40 km/h). No accede a la BD.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cálculo exitoso",
            content = @Content(schema = @Schema(implementation = DistanciaResponseDTO.class)))
    })
    @GetMapping("/calcular-distancia")
    public ResponseEntity<DistanciaResponseDTO> calcularDistancia(
            @Parameter(description = "Latitud origen") @RequestParam Double lat1,
            @Parameter(description = "Longitud origen") @RequestParam Double lon1,
            @Parameter(description = "Latitud destino") @RequestParam Double lat2,
            @Parameter(description = "Longitud destino") @RequestParam Double lon2) {
        return ResponseEntity.ok(service.calcularDistancia(lat1, lon1, lat2, lon2));
    }
}
