package com.logistica.ms_notifications.dto;

import com.logistica.ms_notifications.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDTO {

    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;

    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long destinatarioId;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    private String origen;

    private String metadata;
}