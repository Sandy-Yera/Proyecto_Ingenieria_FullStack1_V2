package com.logistica.ms_price_engine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La categoría no puede ser nula")
    @Column(nullable = false, unique = true)
    private Categoria categoria;

    @NotNull(message = "El costo por hora del técnico no puede ser nulo")
    @Column(nullable = false)
    @PositiveOrZero(message = "El costo por hora del técnico debe ser un valor positivo o cero")
    private Double costoHoraTecnico;

    @NotNull(message = "El costo del material por unidad no puede ser nulo")
    @Column(nullable = false)
    @PositiveOrZero(message = "El costo del material por unidad debe ser un valor positivo o cero")
    private Double costoMaterialPorUnidad;
}