package com.logistica.ms_buildings.dto;

import lombok.Data;

@Data
public class EdificioResponseDTO {
    private Long id;
    private String nombreEdificio;
    private String direccion;
    private String comuna;
    private String nombreAdministrador;
    private String rutAdministrador;
    private String telefonoConserjeria;
    private Integer totalDepartamentos;
    private Double latitud;
    private Double longitud;
}