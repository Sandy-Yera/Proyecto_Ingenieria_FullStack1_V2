package com.logistica.ms_buildings.controller;

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

import com.logistica.ms_buildings.dto.EdificioRequestDTO;
import com.logistica.ms_buildings.dto.EdificioResponseDTO;
import com.logistica.ms_buildings.service.EdificioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * CONTROLADOR REST — EdificioController
 * Responsabilidad exclusiva: recibir peticiones HTTP, delegar al servicio y devolver respuestas.
 * NO contiene lógica de negocio. Solo orquesta el flujo entre el cliente y la capa de servicio.
 *
 * Base URL: /api/edificios
 */
@RestController
@RequestMapping("/api/edificios")
@RequiredArgsConstructor
@Tag(name = "Edificios", description = "Gestión de edificios administrados en el sistema BRM")
public class EdificioController {

    private final EdificioService edificioService;

    /**
     * POST /api/edificios
     * Registra un nuevo edificio en el sistema.
     * Retorna 201 CREATED con el recurso creado.
     */
    @Operation(summary = "Crear edificio", description = "Registra un nuevo edificio en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Edificio creado",
            content = @Content(schema = @Schema(implementation = EdificioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "RUT de administrador ya asignado a otro edificio", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EdificioResponseDTO> crearEdificio(
            @Valid @RequestBody EdificioRequestDTO dto) {
        EdificioResponseDTO creado = edificioService.crearEdificio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * GET /api/edificios
     * Lista todos los edificios registrados.
     * Retorna 200 OK con la lista, o 204 NO CONTENT si no hay registros.
     */
    @Operation(summary = "Listar edificios", description = "Retorna todos los edificios registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin edificios", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<EdificioResponseDTO>> listarEdificios() {
        List<EdificioResponseDTO> listado = edificioService.listarEdificios();
        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    /**
     * GET /api/edificios/{id}
     * Obtiene un edificio por su ID.
     * Retorna 200 OK con el recurso encontrado, o lanza EntityNotFoundException (404).
     */
    @Operation(summary = "Obtener edificio por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Edificio encontrado",
            content = @Content(schema = @Schema(implementation = EdificioResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Edificio no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EdificioResponseDTO> obtenerEdificioPorId(
            @Parameter(description = "ID del edificio") @PathVariable Long id) {
        return ResponseEntity.ok(edificioService.obtenerEdificioPorId(id));
    }

    /**
     * PUT /api/edificios/{id}
     * Actualiza los datos de un edificio existente.
     * Retorna 200 OK con el recurso actualizado.
     */
    @Operation(summary = "Actualizar edificio")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Edificio actualizado",
            content = @Content(schema = @Schema(implementation = EdificioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Coordenadas GPS faltantes", content = @Content),
        @ApiResponse(responseCode = "404", description = "Edificio no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "RUT de administrador ya asignado a otro edificio", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EdificioResponseDTO> actualizarEdificio(
            @Parameter(description = "ID del edificio") @PathVariable Long id,
            @Valid @RequestBody EdificioRequestDTO dto) {
        return ResponseEntity.ok(edificioService.actualizarEdificio(id, dto));
    }

    /**
     * DELETE /api/edificios/{id}
     * Elimina un edificio por su ID.
     * Retorna 204 NO CONTENT si la operación fue exitosa.
     */
    @Operation(summary = "Eliminar edificio")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Edificio eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Edificio no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEdificio(
            @Parameter(description = "ID del edificio") @PathVariable Long id) {
        edificioService.eliminarEdificio(id);
        return ResponseEntity.noContent().build();
    }
}
