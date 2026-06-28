package com.logistica.ms_payments.dto;

import com.logistica.ms_payments.model.EstadoPago;
import com.logistica.ms_payments.model.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private Long id;
    private Long facturaId;
    private Double monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private String referencia;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaActualizacion;
    private String observaciones;
}