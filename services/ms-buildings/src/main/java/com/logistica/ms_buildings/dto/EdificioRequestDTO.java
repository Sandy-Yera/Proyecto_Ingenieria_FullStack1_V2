package com.logistica.ms_buildings.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EdificioRequestDTO {

    @NotBlank(message = "El nombre del edificio es obligatorio")
    @Size(max = 100)
    private String nombreEdificio;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "El nombre del administrador es obligatorio")
    private String nombreAdministrador;

    @NotBlank(message = "El RUT del administrador es obligatorio")
    @Pattern(regexp = "^[0-9]+-[0-9kK]{1}$", message = "Formato de RUT inválido (ej: 12345678-9)")
    private String rutAdministrador;

    @NotNull(message = "El telefono es obligatorio")
    private String telefonoConserjeria;

    @Min(value = 1, message = "El edificio debe tener al menos 1 departamento")
    @NotNull(message = "El total de departamentos es obligatorio")
    private Integer totalDepartamentos;

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
}