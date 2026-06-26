package com.logistica.ms_purchase.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {

    private Long id;
    private Long materialId;
    private String proveedor;
    private Integer cantidad;
    private Double precioUnitario;
    private Double totalCosto;
    private LocalDateTime createdAt;
}
