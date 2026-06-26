package com.logistica.ms_price_engine.dto;

import com.logistica.ms_price_engine.model.Categoria;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrecioRequestDTO {
    @NotNull(message = "La categoría no puede ser nula")
    Categoria categoria;

    @NotNull(message = "Las horas de trabajo no pueden ser nulas")
    @DecimalMin(value = "0.1", inclusive = true, message = "Las horas de trabajo deben ser un valor positivo o cero")
    Double horasTrabajo;

    @NotNull(message = "La unidad de material no puede ser nula")
    @Min(value = 1, message = "La unidad de material debe ser al menos 1")
    Integer unidadesMaterial;
}
