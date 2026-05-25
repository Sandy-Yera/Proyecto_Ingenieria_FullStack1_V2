package com.logistica.ms_security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logistica.ms_security.dto.UserDeletedEvent;
import com.logistica.ms_security.repository.RoleAssignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDeletionEventConsumer {

    private final RoleAssignmentRepository roleAssignmentRepository;
    private final KafkaLogProducer logProducer;
    private final ObjectMapper objectMapper;

    public UserDeletionEventConsumer(RoleAssignmentRepository repo,
                                      KafkaLogProducer logProducer,
                                      ObjectMapper objectMapper) {
        this.roleAssignmentRepository = repo;
        this.logProducer = logProducer;
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "user-deleted-topic", groupId = "ms-security-consumer-group")
    @Transactional
    public void onUserDeleted(String message) {
        try {
            UserDeletedEvent event = objectMapper.readValue(message, UserDeletedEvent.class);
            Long userId = event.getUserId();
            String traceId = event.getTraceId();

            roleAssignmentRepository.deleteAllByIdUser(userId);

            logProducer.sendLog("INFO",
                    "Limpieza reactiva completada: Roles del usuario ID " + userId
                    + " eliminados por evento Kafka. | TraceId: " + traceId);
        } catch (Exception e) {
            log.error("[ms-security] Error procesando evento user-deleted: {}", e.getMessage(), e);
        }
    }
}