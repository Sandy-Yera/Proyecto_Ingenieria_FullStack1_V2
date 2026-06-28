package com.logistica.ms_payments.client;

import com.logistica.ms_payments.dto.feign.BillingCambioEstadoRequestDTO;
import com.logistica.ms_payments.dto.feign.FacturaFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-billing", path = "/api/billing")
public interface BillingClient {

    @GetMapping("/facturas/{id}")
    FacturaFeignDTO obtenerFactura(@PathVariable("id") Long id);

    @PutMapping("/facturas/{id}/pagar")
    FacturaFeignDTO pagarFactura(@PathVariable("id") Long id,
                                 @RequestBody BillingCambioEstadoRequestDTO dto);
}