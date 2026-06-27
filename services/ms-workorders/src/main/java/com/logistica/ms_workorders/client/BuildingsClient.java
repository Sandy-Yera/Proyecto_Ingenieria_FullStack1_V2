package com.logistica.ms_workorders.client;

import com.logistica.ms_workorders.dto.feign.BuildingFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-buildings", path = "/api/edificios")
public interface BuildingsClient {

    @GetMapping("/{id}")
    BuildingFeignDTO obtenerEdificioPorId(@PathVariable("id") Long id);
}