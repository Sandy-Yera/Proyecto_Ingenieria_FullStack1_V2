package com.logistica.ms_staff.service;

import com.logistica.ms_staff.dto.LogEvent;
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

        // Usamos .copy() para crear una copia privada exclusiva.
        // Previene la contaminación del ObjectMapper compartido por Spring en ms-staff.
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendLog(String level, String message) {
        try {
            LogEvent log = new LogEvent("ms-staff", level, message, Instant.now());
            String jsonLog = objectMapper.writeValueAsString(log);
            kafkaTemplate.send(TOPIC, jsonLog);
        } catch (Exception e) {
            System.err.println("[ms-staff] Error enviando log a Kafka: " + e.getMessage());
        }
    }
}