package com.logistica.ms_quotes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El name debe coincidir exactamente con el spring.application.name de ms-users registrado en Eureka
@FeignClient(name = "ms-users", path = "/api/users")
public interface UserClient {

    // Este método mapea la petición GET remota que busca al usuario por su ID
    @GetMapping("/{id}")
    ResponseEntity<?> obtenerUsuarioPorId(@PathVariable("id") Long id);
}