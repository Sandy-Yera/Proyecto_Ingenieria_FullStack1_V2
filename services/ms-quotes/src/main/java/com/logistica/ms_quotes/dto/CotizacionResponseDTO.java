package com.logistica.ms_quotes.dto;

import com.logistica.ms_quotes.model.Category;
import com.logistica.ms_quotes.model.Status;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CotizacionResponseDTO {
    private Long id;
    private Long userId;
    private Long buildingId;
    private String description;
    private Category category;
    private Double estimatedAmount;
    private Status status;
    private LocalDateTime createdAt;
}