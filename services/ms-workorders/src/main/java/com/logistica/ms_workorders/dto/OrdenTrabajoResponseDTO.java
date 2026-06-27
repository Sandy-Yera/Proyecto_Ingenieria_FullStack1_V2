package com.logistica.ms_workorders.dto;

import com.logistica.ms_workorders.model.Categoria;
import com.logistica.ms_workorders.model.EstadoOrden;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoResponseDTO {

    private Long id;
    private Long buildingId;
    private Long quoteId;
    private Long tecnicoId;
    private EstadoOrden estado;
    private String descripcion;
    private Categoria categoria;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String observaciones;
}