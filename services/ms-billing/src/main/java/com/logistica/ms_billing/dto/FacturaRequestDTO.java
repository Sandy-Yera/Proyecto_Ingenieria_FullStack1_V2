package com.logistica.ms_billing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequestDTO {

    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    private Long workOrderId;

    private Double montoManual;
}