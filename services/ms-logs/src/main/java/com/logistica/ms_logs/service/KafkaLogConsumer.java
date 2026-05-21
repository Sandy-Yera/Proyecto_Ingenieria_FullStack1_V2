package com.logistica.ms_logs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.logistica.ms_logs.model.LogEntity;
import com.logistica.ms_logs.repository.LogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant; // 🌟 Nueva importación para manejo de tiempo nativo

@Service
public class KafkaLogConsumer {

    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    public KafkaLogConsumer(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "queue-logs", groupId = "logs-group")
    public void consumeLog(String messageJson) {
        try {
            JsonNode node = objectMapper.readTree(messageJson);

            LogEntity logEntity = new LogEntity();
            logEntity.setServiceName(node.path("serviceName").asText());
            logEntity.setLevel(node.path("level").asText());
            logEntity.setMessage(node.path("message").asText());
            
            // 🟢 SOLUCIÓN BAJO 3: Reconstrucción del tipo temporal nativo.
            // Parseamos el String ISO-8601 de Kafka de vuelta a un objeto Instant 
            // antes de guardarlo en la base de datos.
            String timestampText = node.path("timestamp").asText();
            if (timestampText != null && !timestampText.isEmpty()) {
                logEntity.setTimestamp(Instant.parse(timestampText));
            } else {
                logEntity.setTimestamp(Instant.now()); // Fallback seguro por si viniera vacío
            }

            logRepository.save(logEntity);

            System.out.println("[Kafka] Log guardado: [" + logEntity.getLevel() + "] " + logEntity.getServiceName() + " - " + logEntity.getMessage());

        } catch (Exception e) {
            System.err.println("[Kafka] Error al procesar log: " + e.getMessage());
        }
    }
}