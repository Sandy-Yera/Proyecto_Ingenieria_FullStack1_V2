package com.logistica.ms_security.controller;

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

import com.logistica.ms_security.dto.RoleRequestDTO;  // 🟢 Nuevo: Import Request DTO
import com.logistica.ms_security.dto.RoleResponseDTO; // 🟢 Nuevo: Import Response DTO
import com.logistica.ms_security.service.IRoleService;

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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestión de roles del sistema BRM")
public class RoleController {

    private final IRoleService roleService;

    // CREAR
    @Operation(summary = "Crear rol", description = "Registra un nuevo rol en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Rol creado",
            content = @Content(schema = @Schema(implementation = RoleResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Nombre de rol duplicado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RoleResponseDTO> crearRole(@Valid @RequestBody RoleRequestDTO roleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.crearRole(roleDTO));
    }

    // LEER
    @Operation(summary = "Listar roles", description = "Retorna todos los roles registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin roles", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> listarRole() {
        List<RoleResponseDTO> listado = roleService.listarRole();

        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    // ACTUALIZAR
    @Operation(summary = "Actualizar rol")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualizado",
            content = @Content(schema = @Schema(implementation = RoleResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Nombre de rol duplicado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> actualizarRole(
            @Valid @RequestBody RoleRequestDTO datosActualizados,
            @Parameter(description = "ID del rol") @PathVariable Long id) {
        return ResponseEntity.ok(roleService.actualizarRole(id, datosActualizados));
    }

    // ELIMINAR
    @Operation(summary = "Eliminar rol")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Rol eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRole(
            @Parameter(description = "ID del rol") @PathVariable Long id) {
        roleService.eliminarRole(id);
        return ResponseEntity.noContent().build();
    }
}