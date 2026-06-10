package com.logistica.ms_quotes.dto;

import com.logistica.ms_quotes.model.Category;
import com.logistica.ms_quotes.model.Status;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE ENTRADA (REQUEST) — CotizacionRequestDTO
 * Recibe y valida los datos enviados por el cliente al crear o actualizar una Cotizacion.
 * Desacopla la capa de transporte HTTP de la entidad JPA, evitando exponer
 * el modelo de persistencia directamente al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID del edificio es obligatorio")
    private Long buildingId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    private Category category;

    @NotNull(message = "El monto estimado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto estimado debe ser mayor a 0")
    private Double estimatedAmount;

    // El status en creación es opcional: si no se envía, el servicio asigna PENDING por defecto
    private Status status;
}
