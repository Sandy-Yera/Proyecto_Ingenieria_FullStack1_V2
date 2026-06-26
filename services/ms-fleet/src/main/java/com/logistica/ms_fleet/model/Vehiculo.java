package com.logistica.ms_fleet.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20, message = "La placa no puede superar los 20 caracteres")
    @Column(name = "placa", nullable = false, unique = true, length = 20)
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede superar los 100 caracteres")
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1990, message = "El año debe ser 1990 o posterior")
    @Max(value = 2100, message = "El año no es válido")
    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "ACTIVO";
        }
    }
}
