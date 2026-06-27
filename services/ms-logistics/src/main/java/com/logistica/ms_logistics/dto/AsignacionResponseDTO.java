package com.logistica.ms_logistics.dto;

import com.logistica.ms_logistics.model.EstadoLogistica;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionResponseDTO {

    private Long id;
    private Long ordenTrabajoId;
    private Long vehiculoId;
    private Long tecnicoId;
    private Double latitudOrigen;
    private Double longitudOrigen;
    private Double latitudDestino;
    private Double longitudDestino;
    private Double distanciaKm;
    private Integer tiempoEstimadoMinutos;
    private EstadoLogistica estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private String observaciones;
}
