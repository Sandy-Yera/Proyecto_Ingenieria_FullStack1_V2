package com.logistica.ms_logistics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionRequestDTO {

    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    private Long ordenTrabajoId;

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "La latitud de origen es obligatoria")
    private Double latitudOrigen;

    @NotNull(message = "La longitud de origen es obligatoria")
    private Double longitudOrigen;

    @NotNull(message = "La latitud de destino es obligatoria")
    private Double latitudDestino;

    @NotNull(message = "La longitud de destino es obligatoria")
    private Double longitudDestino;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDateTime fechaSalida;
}
