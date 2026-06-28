package com.logistica.ms_billing.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteFeignDTO {
    private Long id;
    private Double estimatedAmount;
}