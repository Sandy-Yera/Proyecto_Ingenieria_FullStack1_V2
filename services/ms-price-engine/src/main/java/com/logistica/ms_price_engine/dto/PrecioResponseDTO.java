package com.logistica.ms_price_engine.dto;

import com.logistica.ms_price_engine.model.Categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrecioResponseDTO {
    Categoria categoria;
    Double horasTrabajo;
    Integer unidadesMaterial;
    Double costoLaboral;
    Double costoMateriales;
    Double montoTotal;
}
