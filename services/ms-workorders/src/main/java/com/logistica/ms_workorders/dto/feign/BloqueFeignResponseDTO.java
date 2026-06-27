package com.logistica.ms_workorders.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueFeignResponseDTO {
    private Long id;
    private Long tecnicoId;
    private LocalDateTime inicio;
    private LocalDateTime fin;
}