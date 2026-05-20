package com.logistica.user.service;

import com.logistica.user.dto.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        
        // 🟠 SOLUCIÓN ALTO 2: Usamos .copy() para crear una instancia privada aislada.
        // Esto evita mutar el bean singleton de Spring y protege la concurrencia.
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendLog(String level, String message) {
        try {
            LogEvent log = new LogEvent("ms-users", level, message, Instant.now());
            String jsonLog = objectMapper.writeValueAsString(log);
            kafkaTemplate.send(TOPIC, jsonLog);
        } catch (Exception e) {
            System.err.println("[ms-users] Error enviando log a Kafka: " + e.getMessage());
        }
    }
}