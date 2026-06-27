package com.logistica.ms_logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones_logisticas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionLogistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordenTrabajoId;

    @Column(nullable = false)
    private Long vehiculoId;

    @Column(nullable = false)
    private Long tecnicoId;

    @Column(nullable = false)
    private Double latitudOrigen;

    @Column(nullable = false)
    private Double longitudOrigen;

    @Column(nullable = false)
    private Double latitudDestino;

    @Column(nullable = false)
    private Double longitudDestino;

    @Column(nullable = false)
    private Double distanciaKm;

    @Column(nullable = false)
    private Integer tiempoEstimadoMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLogistica estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaSalida;

    private LocalDateTime fechaLlegada;

    private String observaciones;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoLogistica.PLANIFICADO;
        }
    }
}
