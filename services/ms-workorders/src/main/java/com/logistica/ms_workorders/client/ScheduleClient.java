package com.logistica.ms_workorders.client;

import com.logistica.ms_workorders.dto.feign.BloqueFeignRequestDTO;
import com.logistica.ms_workorders.dto.feign.BloqueFeignResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-schedule", path = "/api/schedule")
public interface ScheduleClient {

    @PostMapping
    BloqueFeignResponseDTO crearBloque(@RequestBody BloqueFeignRequestDTO dto);
}