package com.logistica.ms_logistics.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoFeignDTO {
    private Long id;
    private String placa;
}
