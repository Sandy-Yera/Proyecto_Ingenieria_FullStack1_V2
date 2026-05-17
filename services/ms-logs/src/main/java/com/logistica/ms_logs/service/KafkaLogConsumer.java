package com.logistica.ms_logs.service;

import com.logistica.ms_logs.model.LogEntity;
import com.logistica.ms_logs.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogConsumer {

    private final LogRepository logRepository;
    private final ObjectMapper objectMapper; // Para transformar el JSON String a Objeto Java

    public KafkaLogConsumer(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "queue-logs", groupId = "logs-group")
    public void consumeLog(String messageJson) {
        try {
            // 1. Parseamos el JSON que viene de Kafka directamente a nuestra Entidad
            LogEntity logEntity = objectMapper.readValue(messageJson, LogEntity.class);
            
            // 2. Guardamos de forma asíncrona en MySQL
            logRepository.save(logEntity);
            
            // 3. Pintamos en la consola de Docker para verificar visualmente que llegó
            System.out.println("[Kafka] Log guardado con éxito: " + logEntity.getMessage());
            
        } catch (Exception e) {
            System.err.println("Error al procesar el log recibido de Kafka: " + e.getMessage());
        }
    }
}