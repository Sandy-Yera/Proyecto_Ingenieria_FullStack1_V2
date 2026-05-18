package com.logistica.ms_auth.service;

import com.logistica.ms_auth.dto.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class KafkaLogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    // OPTIMIZACIÓN: Definida como constante de clase estática e inmutable
    private static final String TOPIC = "queue-logs";

    public KafkaLogProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendLog(String level, String message) {
        try {
            // CORREGIDO Y ADAPTADO: Pasamos Instant.now() directamente como objeto de tiempo 
            // para cumplir con el contrato del DTO profesional unificado.
            LogEvent log = new LogEvent("ms-auth", level, message, Instant.now());
            String jsonLog = objectMapper.writeValueAsString(log);

            // Enviamos el mensaje de forma totalmente asíncrona al broker de Kafka
            kafkaTemplate.send(TOPIC, jsonLog);
        } catch (Exception e) {
            // Log local de contingencia en Standard Error para salvaguardar el flujo principal
            System.err.println("Error crítico enviando log a Kafka desde ms-auth: " + e.getMessage());
        }
    }
}