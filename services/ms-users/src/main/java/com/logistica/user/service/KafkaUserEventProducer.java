package com.logistica.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logistica.user.dto.UserDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Slf4j
@Service
public class KafkaUserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String USER_DELETED_TOPIC = "user-deleted-topic";

    public KafkaUserEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void publishUserDeleted(Long userId, String traceId) {
        try {
            UserDeletedEvent event = new UserDeletedEvent(userId, traceId, Instant.now());
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(USER_DELETED_TOPIC, String.valueOf(userId), json);
            log.info("[ms-users] Evento user-deleted-topic publicado para userId={} traceId={}", 
                     userId, traceId);
        } catch (Exception e) {
            // El log nunca debe romper el flujo principal
            log.error("[ms-users] Error publicando evento de eliminación: {}", e.getMessage());
        }
    }
}