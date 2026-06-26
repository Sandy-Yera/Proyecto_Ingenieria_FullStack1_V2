package com.logistica.ms_schedule.controller;

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

import com.logistica.ms_schedule.dto.BloqueHorarioRequestDTO;
import com.logistica.ms_schedule.dto.BloqueHorarioResponseDTO;
import com.logistica.ms_schedule.service.BloqueHorarioService;

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
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Tag(name = "Bloques Horarios", description = "Gestión de bloques horarios de técnicos con validación anti-solapamiento")
public class BloqueHorarioController {

    private final BloqueHorarioService bloqueHorarioService;

    @Operation(
        summary = "Crear bloque horario",
        description = "Registra un bloque horario para un técnico. Rechaza el bloque si se solapa con otro existente del mismo técnico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Bloque creado exitosamente",
            content = @Content(schema = @Schema(implementation = BloqueHorarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o fin anterior al inicio", content = @Content),
        @ApiResponse(responseCode = "409", description = "Solapamiento detectado con otro bloque del técnico", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BloqueHorarioResponseDTO> crearBloque(@Valid @RequestBody BloqueHorarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueHorarioService.crearBloque(dto));
    }

    @Operation(summary = "Listar todos los bloques", description = "Retorna todos los bloques horarios registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin bloques registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<BloqueHorarioResponseDTO>> listarBloques() {
        List<BloqueHorarioResponseDTO> lista = bloqueHorarioService.listarBloques();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener bloque por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bloque encontrado",
            content = @Content(schema = @Schema(implementation = BloqueHorarioResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BloqueHorarioResponseDTO> obtenerBloquePorId(
            @Parameter(description = "ID del bloque") @PathVariable Long id) {
        return ResponseEntity.ok(bloqueHorarioService.obtenerBloquePorId(id));
    }

    @Operation(summary = "Listar bloques por técnico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "El técnico no tiene bloques", content = @Content)
    })
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<List<BloqueHorarioResponseDTO>> listarPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long tecnicoId) {
        List<BloqueHorarioResponseDTO> lista = bloqueHorarioService.listarPorTecnico(tecnicoId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(
        summary = "Actualizar bloque horario",
        description = "Modifica un bloque horario existente. Aplica la misma validación anti-solapamiento excluyendo el propio bloque."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bloque actualizado",
            content = @Content(schema = @Schema(implementation = BloqueHorarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Solapamiento detectado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<BloqueHorarioResponseDTO> actualizarBloque(
            @PathVariable Long id, @Valid @RequestBody BloqueHorarioRequestDTO dto) {
        return ResponseEntity.ok(bloqueHorarioService.actualizarBloque(id, dto));
    }

    @Operation(summary = "Eliminar bloque horario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Bloque eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBloque(@PathVariable Long id) {
        bloqueHorarioService.eliminarBloque(id);
        return ResponseEntity.noContent().build();
    }
}
