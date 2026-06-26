package com.logistica.ms_quotes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.logistica.ms_quotes.dto.PrecioRequestDTO;
import com.logistica.ms_quotes.dto.PrecioResponseDTO;

@FeignClient(name = "ms-price-engine", path = "/api/precios")
public interface PriceEngineClient {
    @PostMapping("/calcular")
    PrecioResponseDTO calcularPrecio(@RequestBody PrecioRequestDTO dto);
}
