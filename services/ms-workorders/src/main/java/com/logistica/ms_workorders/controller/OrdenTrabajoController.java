package com.logistica.ms_workorders.controller;

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

import com.logistica.ms_workorders.dto.AsignarTecnicoRequestDTO;
import com.logistica.ms_workorders.dto.CambioEstadoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoResponseDTO;
import com.logistica.ms_workorders.model.EstadoOrden;
import com.logistica.ms_workorders.service.OrdenTrabajoService;

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
@RequestMapping("/api/workorders")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Trabajo", description = "Máquina de estados para gestión de órdenes de trabajo BRM")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;

    @Operation(summary = "Crear orden de trabajo", description = "Registra una nueva orden en estado PENDING.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente",
            content = @Content(schema = @Schema(implementation = OrdenTrabajoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Edificio o cotización no encontrados", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrdenTrabajoResponseDTO> crearOrden(@Valid @RequestBody OrdenTrabajoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenTrabajoService.crearOrden(dto));
    }

    @Operation(summary = "Listar todas las órdenes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin órdenes registradas", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OrdenTrabajoResponseDTO>> listarTodas() {
        List<OrdenTrabajoResponseDTO> lista = ordenTrabajoService.listarTodas();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener orden por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada",
            content = @Content(schema = @Schema(implementation = OrdenTrabajoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la orden") @PathVariable Long id) {
        return ResponseEntity.ok(ordenTrabajoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar órdenes por estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin órdenes con ese estado", content = @Content)
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenTrabajoResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado de la orden") @PathVariable EstadoOrden estado) {
        List<OrdenTrabajoResponseDTO> lista = ordenTrabajoService.listarPorEstado(estado);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar órdenes por edificio")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin órdenes para ese edificio", content = @Content)
    })
    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<OrdenTrabajoResponseDTO>> listarPorBuilding(
            @Parameter(description = "ID del edificio") @PathVariable Long buildingId) {
        List<OrdenTrabajoResponseDTO> lista = ordenTrabajoService.listarPorBuilding(buildingId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar órdenes por técnico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin órdenes para ese técnico", content = @Content)
    })
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<List<OrdenTrabajoResponseDTO>> listarPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long tecnicoId) {
        List<OrdenTrabajoResponseDTO> lista = ordenTrabajoService.listarPorTecnico(tecnicoId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Asignar técnico", description = "Transición PENDING → ASSIGNED. Crea bloque horario en ms-schedule.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Técnico asignado",
            content = @Content(schema = @Schema(implementation = OrdenTrabajoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido o error de bloque horario", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden o técnico no encontrados", content = @Content)
    })
    @PutMapping("/{id}/asignar")
    public ResponseEntity<OrdenTrabajoResponseDTO> asignarTecnico(
            @PathVariable Long id, @Valid @RequestBody AsignarTecnicoRequestDTO dto) {
        return ResponseEntity.ok(ordenTrabajoService.asignarTecnico(id, dto));
    }

    @Operation(summary = "Iniciar trabajo", description = "Transición ASSIGNED → IN_PROGRESS.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trabajo iniciado",
            content = @Content(schema = @Schema(implementation = OrdenTrabajoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<OrdenTrabajoResponseDTO> iniciarTrabajo(@PathVariable Long id) {
        return ResponseEntity.ok(ordenTrabajoService.iniciarTrabajo(id));
    }

    @Operation(summary = "Completar trabajo", description = "Transición IN_PROGRESS → COMPLETED.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trabajo completado",
            content = @Content(schema = @Schema(implementation = OrdenTrabajoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @PutMapping("/{id}/completar")
    public ResponseEntity<OrdenTrabajoResponseDTO> completarTrabajo(
            @PathVariable Long id, @Valid @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(ordenTrabajoService.completarTrabajo(id, dto));
    }

    @Operation(summary = "Cancelar orden", description = "Transición a CANCELLED desde cualquier estado excepto COMPLETED.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden cancelada",
            content = @Content(schema = @Schema(implementation = OrdenTrabajoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<OrdenTrabajoResponseDTO> cancelarOrden(
            @PathVariable Long id, @Valid @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(ordenTrabajoService.cancelarOrden(id, dto));
    }

    @Operation(summary = "Eliminar orden")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Orden eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenTrabajoService.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }
}