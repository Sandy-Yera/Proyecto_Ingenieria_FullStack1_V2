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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_quotes.dto.CotizacionRequestDTO;
import com.logistica.ms_quotes.dto.CotizacionResponseDTO;
import com.logistica.ms_quotes.model.Status;
import com.logistica.ms_quotes.service.CotizacionService;

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
 * CONTROLADOR REST — CotizacionController
 * Responsabilidad exclusiva: recibir peticiones HTTP, delegar al servicio y devolver respuestas.
 *
 * - Trabaja exclusivamente con CotizacionRequestDTO / CotizacionResponseDTO.
 * - Incluye GET /{id}, GET /usuario/{userId} y GET /estado?status= además del CRUD básico.
 * - Documentado con anotaciones Swagger (@Operation, @ApiResponses, @Tag).
 */
@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
@Tag(name = "Cotizaciones", description = "Gestión de cotizaciones de reparación del sistema BRM")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    // ----------------------------------------------------------------
    // POST /api/cotizaciones
    // ----------------------------------------------------------------
    @Operation(
        summary = "Crear una nueva cotización",
        description = "Registra una nueva solicitud de cotización de reparación. El estado se asigna como PENDING si no se especifica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cotización creada exitosamente",
            content = @Content(schema = @Schema(implementation = CotizacionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<CotizacionResponseDTO> crearCotizacion(
            @Valid @RequestBody CotizacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.crearCotizacion(dto));
    }

    // ----------------------------------------------------------------
    // GET /api/cotizaciones
    // ----------------------------------------------------------------
    @Operation(
        summary = "Listar todas las cotizaciones",
        description = "Retorna el listado completo de cotizaciones registradas en el sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay cotizaciones registradas",
            content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CotizacionResponseDTO>> listarCotizaciones() {
        List<CotizacionResponseDTO> listado = cotizacionService.listarCotizaciones();
        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    // ----------------------------------------------------------------
    // GET /api/cotizaciones/{id}
    // ----------------------------------------------------------------
    @Operation(
        summary = "Obtener cotización por ID",
        description = "Busca y retorna una cotización específica por su identificador único."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cotización encontrada",
            content = @Content(schema = @Schema(implementation = CotizacionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cotización no encontrada",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> obtenerCotizacionPorId(
            @Parameter(description = "ID de la cotización") @PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.obtenerCotizacionPorId(id));
    }

    // ----------------------------------------------------------------
    // GET /api/cotizaciones/usuario/{userId}
    // ----------------------------------------------------------------
    @Operation(
        summary = "Listar cotizaciones por usuario",
        description = "Retorna todas las cotizaciones asociadas a un usuario específico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado correctamente"),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene cotizaciones",
            content = @Content)
    })
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<CotizacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        List<CotizacionResponseDTO> listado = cotizacionService.listarPorUsuario(userId);
        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    // ----------------------------------------------------------------
    // GET /api/cotizaciones/estado?status=PENDING
    // ----------------------------------------------------------------
    @Operation(
        summary = "Filtrar cotizaciones por estado",
        description = "Retorna las cotizaciones filtradas por estado: PENDING, SENT, ACCEPTED o REJECTED."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay cotizaciones con ese estado",
            content = @Content)
    })
    @GetMapping("/estado")
    public ResponseEntity<List<CotizacionResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado de la cotización") @RequestParam Status status) {
        List<CotizacionResponseDTO> listado = cotizacionService.listarPorEstado(status);
        return listado.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(listado);
    }

    // ----------------------------------------------------------------
    // PUT /api/cotizaciones/{id}
    // ----------------------------------------------------------------
    @Operation(
        summary = "Actualizar una cotización",
        description = "Modifica los datos de una cotización existente identificada por su ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cotización actualizada correctamente",
            content = @Content(schema = @Schema(implementation = CotizacionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Cotización no encontrada",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "Transición de estado no permitida",
            content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> actualizarCotizacion(
            @Parameter(description = "ID de la cotización a actualizar") @PathVariable Long id,
            @Valid @RequestBody CotizacionRequestDTO dto) {
        return ResponseEntity.ok(cotizacionService.actualizarCotizacion(id, dto));
    }

    // ----------------------------------------------------------------
    // DELETE /api/cotizaciones/{id}
    // ----------------------------------------------------------------
    @Operation(
        summary = "Eliminar una cotización",
        description = "Elimina permanentemente una cotización por su ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cotización eliminada correctamente",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Cotización no encontrada",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCotizacion(
            @Parameter(description = "ID de la cotización a eliminar") @PathVariable Long id) {
        cotizacionService.eliminarCotizacion(id);
        return ResponseEntity.noContent().build();
    }
}
