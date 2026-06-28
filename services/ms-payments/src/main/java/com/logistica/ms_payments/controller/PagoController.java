package com.logistica.ms_payments.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_payments.dto.CambioEstadoRequestDTO;
import com.logistica.ms_payments.dto.PagoRequestDTO;
import com.logistica.ms_payments.dto.PagoResponseDTO;
import com.logistica.ms_payments.model.EstadoPago;
import com.logistica.ms_payments.model.MetodoPago;
import com.logistica.ms_payments.service.PagoService;

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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Procesamiento de pagos de facturas del sistema BRM")
public class PagoController {

    private final PagoService pagoService;

    @Operation(summary = "Procesar pago", description = "Procesa el pago de una factura PENDIENTE.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago procesado exitosamente",
            content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Monto incorrecto o datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Factura no está PENDIENTE", content = @Content)
    })
    @PostMapping("/pagos")
    public ResponseEntity<PagoResponseDTO> procesarPago(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesarPago(dto));
    }

    @Operation(summary = "Listar todos los pagos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin pagos registrados", content = @Content)
    })
    @GetMapping("/pagos")
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        List<PagoResponseDTO> lista = pagoService.listarTodos();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener pago por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado",
            content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    @GetMapping("/pagos/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPorId(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar pagos por factura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin pagos para esa factura", content = @Content)
    })
    @GetMapping("/pagos/factura/{facturaId}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorFactura(
            @Parameter(description = "ID de la factura") @PathVariable Long facturaId) {
        List<PagoResponseDTO> lista = pagoService.listarPorFactura(facturaId);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar pagos por estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin pagos con ese estado", content = @Content)
    })
    @GetMapping("/pagos/estado/{estado}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado del pago") @PathVariable EstadoPago estado) {
        List<PagoResponseDTO> lista = pagoService.listarPorEstado(estado);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar pagos por método de pago")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin pagos con ese método", content = @Content)
    })
    @GetMapping("/pagos/metodo/{metodoPago}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorMetodo(
            @Parameter(description = "Método de pago") @PathVariable MetodoPago metodoPago) {
        List<PagoResponseDTO> lista = pagoService.listarPorMetodo(metodoPago);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Marcar pago como fallido", description = "Solo pagos en estado COMPLETADO.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago marcado como fallido",
            content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Pago no está COMPLETADO", content = @Content)
    })
    @PutMapping("/pagos/{id}/fallido")
    public ResponseEntity<PagoResponseDTO> marcarFallido(
            @PathVariable Long id, @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(pagoService.marcarFallido(id, dto));
    }
}