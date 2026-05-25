package com.logistica.ms_auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logistica.ms_auth.dto.UserDeletedEvent;
import com.logistica.ms_auth.repository.UserCredencialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDeletionEventConsumer {

    private final UserCredencialRepository userCredencialRepository;
    private final KafkaLogProducer logProducer;
    private final ObjectMapper objectMapper;

    public UserDeletionEventConsumer(UserCredencialRepository repo,
                                      KafkaLogProducer logProducer,
                                      ObjectMapper objectMapper) {
        this.userCredencialRepository = repo;
        this.logProducer = logProducer;
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "user-deleted-topic", groupId = "ms-auth-consumer-group")
    @Transactional
    public void onUserDeleted(String message) {
        try {
            UserDeletedEvent event = objectMapper.readValue(message, UserDeletedEvent.class);
            Long userId = event.getUserId();

            userCredencialRepository.findById(userId).ifPresent(credencial -> {
                // Primero desactivar — el JwtService.validateToken consultará isActive
                credencial.setIsActive(false);
                userCredencialRepository.save(credencial); // flush inmediato del flag

                // Luego eliminar el registro
                userCredencialRepository.deleteById(userId);

                logProducer.sendLog("INFO",
                        "Credenciales y sesión activa del usuario ID " + userId
                        + " invalidadas por evento de eliminación. | TraceId: " + event.getTraceId());
            });
        } catch (Exception e) {
            log.error("[ms-auth] Error procesando user-deleted: {}", e.getMessage(), e);
        }
    }
}