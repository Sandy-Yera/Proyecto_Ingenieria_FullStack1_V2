package com.logistica.ms_billing.client;

import com.logistica.ms_billing.dto.feign.WorkOrderFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-workorders", path = "/api/workorders")
public interface WorkOrdersClient {

    @GetMapping("/{id}")
    WorkOrderFeignDTO obtenerOrden(@PathVariable("id") Long id);
}