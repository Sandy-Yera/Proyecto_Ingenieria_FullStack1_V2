package com.logistica.ms_security.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_security.dto.RoleAssignmentRequestDTO;  // 🟢 Nuevo: Import Request DTO
import com.logistica.ms_security.dto.RoleAssignmentResponseDTO; // 🟢 Nuevo: Import Response DTO
import com.logistica.ms_security.service.IRoleAssignmentService;

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
@RequestMapping("/api/role-assignments")
@RequiredArgsConstructor
@Tag(name = "Asignación de Roles", description = "Gestión de la asignación de roles a usuarios")
public class RoleAssignmentController {

    private final IRoleAssignmentService roleAssignmentService;

    // CREAR
    @Operation(summary = "Crear asignación de rol", description = "Asigna un rol a un usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asignación creada",
            content = @Content(schema = @Schema(implementation = RoleAssignmentResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "El usuario ya tiene asignado ese rol", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RoleAssignmentResponseDTO> crearRoleAssignment(
            @Valid @RequestBody RoleAssignmentRequestDTO assignmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleAssignmentService.crearRoleAssignment(assignmentDTO));
    }

    // LEER
    @Operation(summary = "Listar asignaciones de rol", description = "Retorna todas las asignaciones de rol registradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin asignaciones", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<RoleAssignmentResponseDTO>> listarRoleAssignments() {
        List<RoleAssignmentResponseDTO> listado = roleAssignmentService.listarRoleAssignments();

        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    // ACTUALIZAR
    @Operation(summary = "Actualizar asignación de rol")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignación actualizada",
            content = @Content(schema = @Schema(implementation = RoleAssignmentResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoleAssignmentResponseDTO> actualizarRoleAssignment(
            @Valid @RequestBody RoleAssignmentRequestDTO datosActualizados,
            @Parameter(description = "ID de la asignación") @PathVariable Long id) {
        return ResponseEntity.ok(roleAssignmentService.actualizarRoleAssignment(id, datosActualizados));
    }

    // ELIMINAR
    @Operation(summary = "Eliminar asignación de rol")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asignación eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRoleAssignment(
            @Parameter(description = "ID de la asignación") @PathVariable @NonNull Long id) {
        roleAssignmentService.eliminarRoleAssignment(id);
        return ResponseEntity.noContent().build();
    }
}