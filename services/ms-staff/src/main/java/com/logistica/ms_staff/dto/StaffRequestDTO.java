package com.logistica.ms_staff.dto;

import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.model.Experiencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequestDTO {

    /**
     * ID proveniente de ms-auth y ms-users.
     * Se recibe desde el JSON (sin READ_ONLY) porque no es autoincremental.
     */
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long id;
 
    @NotNull(message = "La especialidad es obligatoria")
    private Especialidad especialidad;
 
    @NotNull(message = "El nivel de experiencia es obligatorio")
    private Experiencia Experiencia;
 
    @NotNull(message = "El estado de disponibilidad es obligatorio")
    private Boolean estadoDisponibilidad;
 
    @NotBlank(message = "La certificación SEC es obligatoria")
    private String certificacionSec;
}