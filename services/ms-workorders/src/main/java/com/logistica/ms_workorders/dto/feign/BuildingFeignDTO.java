package com.logistica.ms_workorders.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingFeignDTO {
    private Long id;
    private String nombreEdificio;
}