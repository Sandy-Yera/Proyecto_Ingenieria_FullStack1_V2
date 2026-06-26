package com.logistica.ms_quotes.dto;

import java.time.LocalDateTime;

import com.logistica.ms_quotes.model.Categoria;
import com.logistica.ms_quotes.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE SALIDA (RESPONSE) — CotizacionResponseDTO
 * Representa los datos de una Cotizacion que se devuelven al cliente tras cada
 * operación.
 * Incluye el ID generado y el timestamp de creación, ambos campos de solo
 * lectura.
 * Desacopla la respuesta HTTP de la entidad JPA interna.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionResponseDTO {

    private Long id;
    private Long userId;
    private Long buildingId;
    private String description;
    private Categoria category;
    private Double estimatedAmount;
    private Status status;
    private LocalDateTime createdAt;
}
