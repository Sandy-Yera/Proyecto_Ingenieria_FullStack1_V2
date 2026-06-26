package com.logistica.ms_fleet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GpsLocationDTO {

    @NotNull(message = "La latitud es obligatoria")
    private Double lat;

    @NotNull(message = "La longitud es obligatoria")
    private Double lng;

    @NotBlank(message = "El timestamp es obligatorio")
    private String timestamp;
}
