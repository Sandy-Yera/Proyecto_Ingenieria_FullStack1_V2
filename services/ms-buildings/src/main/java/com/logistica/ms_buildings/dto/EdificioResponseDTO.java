package com.logistica.ms_buildings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE SALIDA (RESPONSE)
 * Representa los datos del Edificio que se devuelven al cliente en cada operación.
 * Incluye el ID generado por la base de datos y todos los atributos relevantes del dominio.
 * Desacopla la respuesta HTTP de la entidad JPA interna.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
