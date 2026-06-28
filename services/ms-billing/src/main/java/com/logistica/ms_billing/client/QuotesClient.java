package com.logistica.ms_billing.client;

import com.logistica.ms_billing.dto.feign.QuoteFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-quotes", path = "/api/cotizaciones")
public interface QuotesClient {

    @GetMapping("/{id}")
    QuoteFeignDTO obtenerCotizacion(@PathVariable("id") Long id);
}