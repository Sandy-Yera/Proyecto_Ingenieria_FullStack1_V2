package com.logistica.ms_workorders.dto;

import com.logistica.ms_workorders.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoRequestDTO {

    @NotNull(message = "El ID del edificio es obligatorio")
    private Long buildingId;

    private Long quoteId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;
}