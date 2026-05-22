package com.logistica.ms_quotes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.logistica.ms_quotes.dto.EdificioResponseDTO; // 🟢 Nuevo: Importamos el DTO de respuesta

@FeignClient(name = "ms-buildings", path = "/api/edificios")
public interface BuildingClient {

    // 🟢 Tipado estricto: Feign deserializa el JSON directamente en nuestro DTO
    @GetMapping("/{id}")
    EdificioResponseDTO obtenerEdificioPorId(@PathVariable("id") Long id);
}