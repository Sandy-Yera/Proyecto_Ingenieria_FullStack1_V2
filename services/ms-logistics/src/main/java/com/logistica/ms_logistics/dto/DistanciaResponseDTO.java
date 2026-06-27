package com.logistica.ms_logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanciaResponseDTO {
    private Double distanciaKm;
    private Integer tiempoEstimadoMinutos;
}
