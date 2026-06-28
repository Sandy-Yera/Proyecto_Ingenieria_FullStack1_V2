package com.logistica.ms_payments.dto;

import com.logistica.ms_payments.model.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotNull(message = "El ID de la factura es obligatorio")
    private Long facturaId;

    @NotNull(message = "El monto es obligatorio")
    private Double monto;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    private String referencia;
}