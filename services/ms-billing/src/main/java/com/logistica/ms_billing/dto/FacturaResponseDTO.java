package com.logistica.ms_billing.dto;

import com.logistica.ms_billing.model.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDTO {

    private Long id;
    private Long workOrderId;
    private Long tecnicoId;
    private Long buildingId;
    private String descripcion;
    private Double montoTotal;
    private EstadoFactura estado;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaPago;
    private String observaciones;
}