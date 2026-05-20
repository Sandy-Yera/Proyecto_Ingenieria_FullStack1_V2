package com.logistica.ms_quotes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El name debe coincidir exactamente con el spring.application.name de ms-buildings en Eureka
@FeignClient(name = "ms-buildings", path = "/api/edificios")
public interface BuildingClient {

    // Este método mapea la petición GET remota que busca el edificio por su ID
    @GetMapping("/{id}")
    ResponseEntity<?> obtenerEdificioPorId(@PathVariable("id") Long id);
}