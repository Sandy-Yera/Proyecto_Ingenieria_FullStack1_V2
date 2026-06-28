package com.logistica.ms_notifications.controller;

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

import com.logistica.ms_notifications.dto.NotificacionRequestDTO;
import com.logistica.ms_notifications.dto.NotificacionResponseDTO;
import com.logistica.ms_notifications.model.TipoNotificacion;
import com.logistica.ms_notifications.service.NotificacionService;

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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones del sistema BRM")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Crear notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Notificación creada",
            content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping("/notificaciones")
    public ResponseEntity<NotificacionResponseDTO> crearNotificacion(
            @Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificacionService.crearNotificacion(dto));
    }

    @Operation(summary = "Listar todas las notificaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin notificaciones", content = @Content)
    })
    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodas() {
        List<NotificacionResponseDTO> lista = notificacionService.listarTodas();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener notificación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación encontrada",
            content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @GetMapping("/notificaciones/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la notificación") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @Operation(summary = "Listar notificaciones por destinatario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin notificaciones para ese destinatario", content = @Content)
    })
    @GetMapping("/notificaciones/destinatario/{destinatarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorDestinatario(
            @Parameter(description = "ID del destinatario") @PathVariable Long destinatarioId) {
        List<NotificacionResponseDTO> lista = notificacionService.listarPorDestinatario(destinatarioId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar notificaciones no leídas por destinatario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin notificaciones no leídas", content = @Content)
    })
    @GetMapping("/notificaciones/no-leidas/{destinatarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarNoLeidas(
            @Parameter(description = "ID del destinatario") @PathVariable Long destinatarioId) {
        List<NotificacionResponseDTO> lista = notificacionService.listarNoLeidas(destinatarioId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Contar notificaciones no leídas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contador retornado")
    })
    @GetMapping("/notificaciones/no-leidas/{destinatarioId}/count")
    public ResponseEntity<Long> contarNoLeidas(
            @Parameter(description = "ID del destinatario") @PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(destinatarioId));
    }

    @Operation(summary = "Listar notificaciones por tipo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin notificaciones de ese tipo", content = @Content)
    })
    @GetMapping("/notificaciones/tipo/{tipo}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorTipo(
            @Parameter(description = "Tipo de notificación") @PathVariable TipoNotificacion tipo) {
        List<NotificacionResponseDTO> lista = notificacionService.listarPorTipo(tipo);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Marcar notificación como leída")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación marcada como leída",
            content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @PutMapping("/notificaciones/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarLeida(id));
    }

    @Operation(summary = "Eliminar notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificación eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}