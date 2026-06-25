package com.logistica.ms_buildings.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE ENTRADA (REQUEST)
 * Recibe y valida los datos enviados por el cliente para crear o actualizar un Edificio.
 * Separa la capa de transporte del modelo JPA, evitando exposición directa de la entidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdificioRequestDTO {

    @NotBlank(message = "El nombre del edificio es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
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

    @NotBlank(message = "El teléfono de conserjería es obligatorio")
    private String telefonoConserjeria;

    @NotNull(message = "El total de departamentos es obligatorio")
    @Min(value = 1, message = "El edificio debe tener al menos 1 departamento")
    private Integer totalDepartamentos;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0",  message = "La latitud debe ser mayor o igual a -90.0")
    @DecimalMax(value = "90.0",   message = "La latitud debe ser menor o igual a 90.0")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.0")
    @DecimalMax(value = "180.0",  message = "La longitud debe ser menor o igual a 180.0")
    private Double longitud;
}
