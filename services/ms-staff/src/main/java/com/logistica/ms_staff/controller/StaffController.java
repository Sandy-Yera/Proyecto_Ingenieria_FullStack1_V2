package com.logistica.ms_staff.controller;

import com.logistica.ms_staff.dto.StaffRequestDTO;
import com.logistica.ms_staff.dto.StaffResponseDTO;
import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.service.StaffService;
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
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@Tag(name = "Staff", description = "Gestión de técnicos del sistema BRM")
public class StaffController {

    private final StaffService staffService;

    // POST /api/v1/staff
    @Operation(summary = "Crear técnico", description = "Registra un nuevo técnico en el staff.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Técnico creado",
            content = @Content(schema = @Schema(implementation = StaffResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<StaffResponseDTO> crearTecnico(@Valid @RequestBody StaffRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.crearTecnico(dto));
    }

    // GET /api/v1/staff/{id}
    @Operation(summary = "Obtener técnico por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Técnico encontrado",
            content = @Content(schema = @Schema(implementation = StaffResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Técnico no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> obtenerTecnicoPorId(
            @Parameter(description = "ID del técnico") @PathVariable Long id) {
        return ResponseEntity.ok(staffService.obtenerTecnicoPorId(id));
    }

    // GET /api/v1/staff
    @Operation(summary = "Listar técnicos", description = "Retorna todos los técnicos registrados.")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> obtenerTodosTecnicos() {
        return ResponseEntity.ok(staffService.obtenerTodosTecnicos());
    }

    // GET /api/v1/staff/especialidad/{especialidad}
    @Operation(summary = "Listar técnicos por especialidad")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<StaffResponseDTO>> obtenerPorEspecialidad(
            @Parameter(description = "Especialidad del técnico") @PathVariable Especialidad especialidad) {
        return ResponseEntity.ok(staffService.obtenerTecnicosPorEspecialidad(especialidad));
    }

    // GET /api/v1/staff/disponibles
    @Operation(summary = "Listar técnicos disponibles")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @GetMapping("/disponibles")
    public ResponseEntity<List<StaffResponseDTO>> obtenerDisponibles() {
        return ResponseEntity.ok(staffService.obtenerTecnicosDisponibles());
    }

    // GET /api/v1/staff/disponibles/{especialidad}
    // Endpoint clave para ms-logistics: asignación inteligente de técnicos
    @Operation(summary = "Listar técnicos disponibles por especialidad",
            description = "Endpoint clave consumido por ms-logistics para la asignación inteligente de técnicos.")
    @ApiResponse(responseCode = "200", description = "Listado retornado")
    @GetMapping("/disponibles/{especialidad}")
    public ResponseEntity<List<StaffResponseDTO>> obtenerDisponiblesPorEspecialidad(
            @Parameter(description = "Especialidad del técnico") @PathVariable Especialidad especialidad) {
        return ResponseEntity.ok(staffService.obtenerTecnicosDisponiblesPorEspecialidad(especialidad));
    }

    // PUT /api/v1/staff/{id}
    @Operation(summary = "Actualizar técnico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Técnico actualizado",
            content = @Content(schema = @Schema(implementation = StaffResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Técnico no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> actualizarTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long id,
            @Valid @RequestBody StaffRequestDTO dto) {
        return ResponseEntity.ok(staffService.actualizarTecnico(id, dto));
    }

    // PATCH /api/v1/staff/{id}/disponibilidad
    @Operation(summary = "Actualizar disponibilidad", description = "Cambia el estado de disponibilidad de un técnico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada",
            content = @Content(schema = @Schema(implementation = StaffResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Técnico no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<StaffResponseDTO> actualizarDisponibilidad(
            @Parameter(description = "ID del técnico") @PathVariable Long id,
            @Parameter(description = "Nuevo estado de disponibilidad") @RequestParam Boolean disponibilidad) {
        return ResponseEntity.ok(staffService.actualizarDisponibilidad(id, disponibilidad));
    }

    // DELETE /api/v1/staff/{id}
    @Operation(summary = "Eliminar técnico")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Técnico eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Técnico no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long id) {
        staffService.eliminarTecnico(id);
        return ResponseEntity.noContent().build();
    }
}