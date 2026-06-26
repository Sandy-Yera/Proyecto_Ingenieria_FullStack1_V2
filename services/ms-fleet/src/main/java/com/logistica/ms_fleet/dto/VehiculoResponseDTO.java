package com.logistica.ms_fleet.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponseDTO {

    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    private Integer capacidad;
    private String estado;
    private LocalDateTime createdAt;
}
