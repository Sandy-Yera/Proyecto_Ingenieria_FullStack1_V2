package com.logistica.ms_notifications.kafka;

import com.logistica.ms_notifications.dto.NotificacionRequestDTO;
import com.logistica.ms_notifications.model.TipoNotificacion;
import com.logistica.ms_notifications.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationKafkaListener.class);

    private final NotificacionService notificacionService;

    @KafkaListener(topics = "fleet-gps-tracking", groupId = "notifications-group")
    public void onVehiculoEnRuta(String payload) {
        log.info("[ms-notifications] Mensaje Kafka recibido del topic fleet-gps-tracking: {}", payload);

        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo(TipoNotificacion.VEHICULO_EN_RUTA);
        dto.setDestinatarioId(0L);
        dto.setMensaje("Vehículo en ruta: " + payload);
        dto.setOrigen("ms-fleet");
        dto.setMetadata(payload);

        notificacionService.crearNotificacion(dto);
    }
}