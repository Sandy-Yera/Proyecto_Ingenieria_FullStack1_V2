package com.logistica.ms_quotes.dto;

import com.logistica.ms_quotes.model.Categoria;
import com.logistica.ms_quotes.model.Status;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO DE ENTRADA (REQUEST) — CotizacionRequestDTO
 * Recibe y valida los datos enviados por el cliente al crear o actualizar una
 * Cotizacion.
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
    private Categoria categoria;

    @NotNull(message = "Las horas de trabajo son obligatorias")
    @DecimalMin(value = "0.1", inclusive = true, message = "Las horas de trabajo deben ser un valor positivo o cero")
    private Double horasTrabajo;

    @NotNull(message = "Las unidades de material son obligatorias")
    @Min(value = 1, message = "Las unidades de material deben ser un valor positivo o cero")
    private Integer unidadesMaterial;

    private Double montoEstimado;

    // El status en creación es opcional: si no se envía, el servicio asigna PENDING
    // por defecto
    private Status status;
}
