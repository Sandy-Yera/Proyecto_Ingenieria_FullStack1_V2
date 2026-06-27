package com.logistica.ms_logistics.client;

import com.logistica.ms_logistics.dto.feign.VehiculoFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-fleet", path = "/api/fleet")
public interface FleetClient {

    @GetMapping("/{id}")
    VehiculoFeignDTO obtenerVehiculo(@PathVariable("id") Long id);
}
