package com.logistica.ms_fleet.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.ms_fleet.dto.GpsLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FleetKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(FleetKafkaProducer.class);
    private static final String TOPIC = "fleet-gps-tracking";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public FleetKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicarUbicacion(Long vehiculoId, GpsLocationDTO location) {
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("vehiculoId", vehiculoId);
        mensaje.put("lat", location.getLat());
        mensaje.put("lng", location.getLng());
        mensaje.put("timestamp", location.getTimestamp());

        try {
            String json = objectMapper.writeValueAsString(mensaje);
            kafkaTemplate.send(TOPIC, String.valueOf(vehiculoId), json);
            log.info("[ms-fleet] GPS publicado al topic '{}': {}", TOPIC, json);
        } catch (JsonProcessingException e) {
            log.error("[ms-fleet] Error al serializar mensaje GPS para vehículo id={}: {}", vehiculoId, e.getMessage());
            throw new RuntimeException("Error al serializar la ubicación GPS", e);
        }
    }
}
