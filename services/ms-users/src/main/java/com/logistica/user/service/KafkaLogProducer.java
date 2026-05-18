package com.logistica.user.service;

import com.logistica.user.dto.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class KafkaLogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String TOPIC = "queue-logs";

    public KafkaLogProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendLog(String level, String message) {
        try {
            // CORREGIDO Y ADAPTADO: Pasamos Instant.now() directamente como un objeto de tiempo.
            // Jackson (ObjectMapper) se encargará de serializarlo en formato ISO-8601 gracias al @JsonFormat.
            LogEvent log = new LogEvent("ms-users", level, message, Instant.now());
            String jsonLog = objectMapper.writeValueAsString(log);

            // Enviamos el mensaje de forma totalmente asíncrona al broker de Kafka
            kafkaTemplate.send(TOPIC, jsonLog);
        } catch (Exception e) {
            // Log local de contingencia para que un fallo en Kafka jamás bote el flujo principal de registro
            System.err.println("Error crítico enviando log a Kafka: " + e.getMessage());
        }
    }
}