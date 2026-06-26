package com.logistica.ms_schedule.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueHorarioRequestDTO {

    @NotNull(message = "El ID del técnico es obligatorio")
    private Long tecnicoId;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime inicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalDateTime fin;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;
}
