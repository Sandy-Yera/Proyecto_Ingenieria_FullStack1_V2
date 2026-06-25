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
public class EdificioController {

    private final EdificioService edificioService;

    /**
     * POST /api/edificios
     * Registra un nuevo edificio en el sistema.
     * Retorna 201 CREATED con el recurso creado.
     */
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
    @GetMapping("/{id}")
    public ResponseEntity<EdificioResponseDTO> obtenerEdificioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(edificioService.obtenerEdificioPorId(id));
    }

    /**
     * PUT /api/edificios/{id}
     * Actualiza los datos de un edificio existente.
     * Retorna 200 OK con el recurso actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EdificioResponseDTO> actualizarEdificio(
            @PathVariable Long id,
            @Valid @RequestBody EdificioRequestDTO dto) {
        return ResponseEntity.ok(edificioService.actualizarEdificio(id, dto));
    }

    /**
     * DELETE /api/edificios/{id}
     * Elimina un edificio por su ID.
     * Retorna 204 NO CONTENT si la operación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEdificio(@PathVariable Long id) {
        edificioService.eliminarEdificio(id);
        return ResponseEntity.noContent().build();
    }
}
