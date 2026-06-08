package com.logistica.ms_buildings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ms_edificio")
public class Edificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre del edificio es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombreEdificio;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false)
    private String direccion;

    @NotBlank(message = "La comuna es obligatoria")
    @Column(nullable = false)
    private String comuna;

    @NotBlank(message = "El nombre del administrador es obligatorio")
    @Column(nullable = false)
    private String nombreAdministrador;

    @NotBlank(message = "El RUT del administrador es obligatorio")
    @Pattern(regexp = "^[0-9]+-[0-9kK]{1}$", message = "Formato de RUT inválido (ej: 12345678-9)")
    @Column(nullable = false, unique = true)
    private String rutAdministrador;

    @NotBlank(message = "El teléfono de conserjería es obligatorio")
    @Column(nullable = false)
    private String telefonoConserjeria;

    @Min(value = 1, message = "El edificio debe tener al menos 1 departamento")
    @NotNull(message = "El total de departamentos es obligatorio")
    @Column(nullable = false)
    private Integer totalDepartamentos;

    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90.0")
    @DecimalMax(value = "90.0",  message = "La latitud debe ser menor o igual a 90.0")
    @NotNull(message = "La latitud es obligatoria")
    @Column(nullable = false)
    private Double latitud;

    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.0")
    @DecimalMax(value = "180.0",  message = "La longitud debe ser menor o igual a 180.0")
    @NotNull(message = "La longitud es obligatoria")
    @Column(nullable = false)
    private Double longitud;
}
