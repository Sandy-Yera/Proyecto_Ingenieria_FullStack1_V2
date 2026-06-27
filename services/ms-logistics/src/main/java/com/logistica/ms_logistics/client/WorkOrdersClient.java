package com.logistica.ms_logistics.client;

import com.logistica.ms_logistics.dto.feign.OrdenTrabajoFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-workorders", path = "/api/workorders")
public interface WorkOrdersClient {

    @GetMapping("/{id}")
    OrdenTrabajoFeignDTO obtenerOrden(@PathVariable("id") Long id);
}
