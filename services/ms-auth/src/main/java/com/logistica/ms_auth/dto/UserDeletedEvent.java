package com.logistica.ms_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeletedEvent {
    private Long userId;
    private String traceId;
    private Instant deletedAt;
}