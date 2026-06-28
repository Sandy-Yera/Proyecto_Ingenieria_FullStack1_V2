package com.logistica.ms_notifications.dto;

import com.logistica.ms_notifications.model.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {

    private Long id;
    private TipoNotificacion tipo;
    private Long destinatarioId;
    private String mensaje;
    private Boolean leida;
    private String origen;
    private String metadata;
    private LocalDateTime fechaCreacion;
}