package com.logistica.ms_inventory.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer stock;
    private String unidad;
    private LocalDateTime createdAt;
}
