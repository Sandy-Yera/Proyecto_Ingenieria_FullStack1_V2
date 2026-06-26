package com.logistica.ms_purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {

    @NotNull(message = "El ID del material es obligatorio")
    private Long materialId;

    @NotBlank(message = "El proveedor es obligatorio")
    @Size(max = 200, message = "El nombre del proveedor no puede superar los 200 caracteres")
    private String proveedor;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    private Double precioUnitario;
}
