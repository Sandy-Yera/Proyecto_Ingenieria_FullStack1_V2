package com.logistica.ms_logistics.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoFeignDTO {
    private Long id;
    private Long tecnicoId;
    private String estado;
    private Long buildingId;
}
