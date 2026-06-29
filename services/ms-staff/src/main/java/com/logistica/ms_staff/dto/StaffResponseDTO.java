package com.logistica.ms_staff.dto;

import java.time.LocalDateTime;

import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.model.Experiencia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {

    private Long id;
    private Especialidad especialidad;
    private Experiencia experiencia;
    private Boolean estadoDisponibilidad;
    private String certificacionSec;
    private LocalDateTime createdAt;
}