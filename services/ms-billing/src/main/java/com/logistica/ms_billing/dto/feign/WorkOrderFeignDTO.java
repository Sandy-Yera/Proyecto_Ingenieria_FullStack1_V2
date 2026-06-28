package com.logistica.ms_billing.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderFeignDTO {
    private Long id;
    private String estado;
    private Long buildingId;
    private Long tecnicoId;
    private Long quoteId;
    private String descripcion;
}