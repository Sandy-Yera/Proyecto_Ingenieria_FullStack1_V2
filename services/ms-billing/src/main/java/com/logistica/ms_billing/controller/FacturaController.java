package com.logistica.ms_billing.controller;

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

import com.logistica.ms_billing.dto.CambioEstadoRequestDTO;
import com.logistica.ms_billing.dto.FacturaRequestDTO;
import com.logistica.ms_billing.dto.FacturaResponseDTO;
import com.logistica.ms_billing.model.EstadoFactura;
import com.logistica.ms_billing.service.FacturaService;

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
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Tag(name = "Facturas", description = "Emisión y gestión del ciclo de vida de facturas BRM")
public class FacturaController {

    private final FacturaService facturaService;

    @Operation(summary = "Emitir factura", description = "Emite una factura para una orden COMPLETED.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Factura emitida",
            content = @Content(schema = @Schema(implementation = FacturaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Orden no COMPLETED o sin monto", content = @Content),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Ya existe factura para esa orden", content = @Content)
    })
    @PostMapping("/facturas")
    public ResponseEntity<FacturaResponseDTO> emitirFactura(@Valid @RequestBody FacturaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.emitirFactura(dto));
    }

    @Operation(summary = "Listar todas las facturas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin facturas", content = @Content)
    })
    @GetMapping("/facturas")
    public ResponseEntity<List<FacturaResponseDTO>> listarTodas() {
        List<FacturaResponseDTO> lista = facturaService.listarTodas();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener factura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura encontrada",
            content = @Content(schema = @Schema(implementation = FacturaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    })
    @GetMapping("/facturas/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la factura") @PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar facturas por orden de trabajo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin facturas para esa orden", content = @Content)
    })
    @GetMapping("/facturas/workorder/{woId}")
    public ResponseEntity<List<FacturaResponseDTO>> listarPorWorkOrder(
            @Parameter(description = "ID de la orden de trabajo") @PathVariable Long woId) {
        List<FacturaResponseDTO> lista = facturaService.listarPorWorkOrder(woId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar facturas por técnico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin facturas para ese técnico", content = @Content)
    })
    @GetMapping("/facturas/tecnico/{tecnicoId}")
    public ResponseEntity<List<FacturaResponseDTO>> listarPorTecnico(
            @Parameter(description = "ID del técnico") @PathVariable Long tecnicoId) {
        List<FacturaResponseDTO> lista = facturaService.listarPorTecnico(tecnicoId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar facturas por estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin facturas con ese estado", content = @Content)
    })
    @GetMapping("/facturas/estado/{estado}")
    public ResponseEntity<List<FacturaResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado de la factura") @PathVariable EstadoFactura estado) {
        List<FacturaResponseDTO> lista = facturaService.listarPorEstado(estado);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Pagar factura", description = "Transición PENDIENTE → PAGADA.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura pagada",
            content = @Content(schema = @Schema(implementation = FacturaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Factura no está PENDIENTE", content = @Content)
    })
    @PutMapping("/facturas/{id}/pagar")
    public ResponseEntity<FacturaResponseDTO> pagarFactura(
            @PathVariable Long id, @Valid @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(facturaService.pagarFactura(id, dto));
    }

    @Operation(summary = "Anular factura", description = "Transición PENDIENTE → ANULADA.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura anulada",
            content = @Content(schema = @Schema(implementation = FacturaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Factura ya está PAGADA", content = @Content)
    })
    @PutMapping("/facturas/{id}/anular")
    public ResponseEntity<FacturaResponseDTO> anularFactura(
            @PathVariable Long id, @Valid @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(facturaService.anularFactura(id, dto));
    }

    @Operation(summary = "Eliminar factura", description = "Solo facturas en estado PENDIENTE.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Factura eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Factura no está PENDIENTE", content = @Content)
    })
    @DeleteMapping("/facturas/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}