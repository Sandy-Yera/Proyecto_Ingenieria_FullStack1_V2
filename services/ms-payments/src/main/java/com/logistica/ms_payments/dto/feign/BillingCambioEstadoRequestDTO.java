package com.logistica.ms_payments.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCambioEstadoRequestDTO {
    private String observaciones;
}