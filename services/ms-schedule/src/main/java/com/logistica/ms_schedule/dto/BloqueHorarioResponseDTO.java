package com.logistica.ms_schedule.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueHorarioResponseDTO {

    private Long id;
    private Long tecnicoId;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String descripcion;
    private LocalDateTime createdAt;
}
