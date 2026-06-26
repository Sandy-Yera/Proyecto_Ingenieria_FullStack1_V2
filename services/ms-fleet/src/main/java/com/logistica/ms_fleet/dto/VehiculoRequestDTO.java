package com.logistica.ms_fleet.dto;

import jakarta.validation.constraints.Max;
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
public class VehiculoRequestDTO {

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20, message = "La placa no puede superar los 20 caracteres")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede superar los 100 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1990, message = "El año debe ser 1990 o posterior")
    @Max(value = 2100, message = "El año no es válido")
    private Integer anio;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;

    private String estado;
}
