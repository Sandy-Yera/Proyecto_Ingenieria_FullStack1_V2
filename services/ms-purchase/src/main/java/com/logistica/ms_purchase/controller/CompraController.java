package com.logistica.ms_purchase.controller;

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

import com.logistica.ms_purchase.dto.CompraRequestDTO;
import com.logistica.ms_purchase.dto.CompraResponseDTO;
import com.logistica.ms_purchase.service.CompraService;

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
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Registro de compras a proveedores con actualización automática de inventario")
public class CompraController {

    private final CompraService compraService;

    @Operation(
        summary = "Registrar compra",
        description = "Registra una compra a proveedor y actualiza automáticamente el stock en ms-inventory."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compra registrada exitosamente",
            content = @Content(schema = @Schema(implementation = CompraResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error de comunicación", content = @Content),
        @ApiResponse(responseCode = "404", description = "Material no encontrado en inventario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CompraResponseDTO> registrarCompra(@Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.registrarCompra(dto));
    }

    @Operation(summary = "Listar compras", description = "Retorna todas las compras registradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin compras registradas", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarCompras() {
        List<CompraResponseDTO> lista = compraService.listarCompras();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener compra por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compra encontrada",
            content = @Content(schema = @Schema(implementation = CompraResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Compra no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerCompraPorId(
            @Parameter(description = "ID de la compra") @PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerCompraPorId(id));
    }

    @Operation(summary = "Listar compras por material")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin compras para ese material", content = @Content)
    })
    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<CompraResponseDTO>> listarPorMaterial(
            @Parameter(description = "ID del material") @PathVariable Long materialId) {
        List<CompraResponseDTO> lista = compraService.listarPorMaterial(materialId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Actualizar compra", description = "Modifica los datos de una compra existente. No actualiza el stock automáticamente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compra actualizada",
            content = @Content(schema = @Schema(implementation = CompraResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Compra no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> actualizarCompra(
            @PathVariable Long id, @Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.ok(compraService.actualizarCompra(id, dto));
    }

    @Operation(summary = "Eliminar compra")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compra eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Compra no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCompra(@PathVariable Long id) {
        compraService.eliminarCompra(id);
        return ResponseEntity.noContent().build();
    }
}
