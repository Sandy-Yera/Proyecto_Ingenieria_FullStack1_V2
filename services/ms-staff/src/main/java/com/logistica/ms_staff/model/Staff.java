package com.logistica.ms_staff.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tecnicos")
public class Staff {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id; // ID proveniente de ms-auth y ms-users, no autoincremental

    @NotNull(message = "La especialidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad", nullable = false)
    private Especialidad especialidad;

    @NotNull(message = "El nivel de experiencia es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_experiencia", nullable = false)
    private Experiencia experiencia;

    @NotNull(message = "El estado de disponibilidad es obligatorio")
    @Column(name = "estado_disponibilidad", nullable = false)
    private Boolean estadoDisponibilidad;

    @NotBlank(message = "La certificación SEC es obligatoria")
    @Column(name = "certificacion_sec", nullable = false, unique = true, length = 50)
    private String certificacionSec;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}