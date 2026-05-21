package com.logistica.ms_quotes.dto;

import com.logistica.ms_quotes.model.Category;
import com.logistica.ms_quotes.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CotizacionRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID del edificio es obligatorio")
    private Long buildingId;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    private Category category;

    @NotNull(message = "El monto estimado es obligatorio")
    private Double estimatedAmount;

    // El estado lo dejamos opcional en el Request ya que el método @PrePersist de la entidad asignará PENDING si viene nulo
    private Status status;
}