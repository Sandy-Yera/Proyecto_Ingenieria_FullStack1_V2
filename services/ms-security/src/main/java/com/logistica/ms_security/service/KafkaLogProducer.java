package com.logistica.ms_security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

// (misma estructura que en ms-users y ms-auth — LogEvent DTO idéntico)
@Service
public class KafkaLogProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "queue-logs";

    public KafkaLogProducer(KafkaTemplate<String, String> kt, ObjectMapper om) {
        this.kafkaTemplate = kt;
        this.objectMapper = om.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendLog(String level, String message) {
        try {
            // LogEvent idéntico al de los otros servicios
            var log = new com.logistica.ms_security.dto.LogEvent(
                    "ms-security", level, message, Instant.now());
            kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(log));
        } catch (Exception e) {
            System.err.println("[ms-security] Error Kafka log: " + e.getMessage());
        }
    }
}