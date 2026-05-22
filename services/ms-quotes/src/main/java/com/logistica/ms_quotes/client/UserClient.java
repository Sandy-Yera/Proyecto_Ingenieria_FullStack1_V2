package com.logistica.ms_quotes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.logistica.ms_quotes.dto.UserResponseDTO; // 🟢 Nuevo: Importamos el DTO de respuesta

@FeignClient(name = "ms-users", path = "/api/users")
public interface UserClient {

    // 🟢 Tipado estricto: Deserialización directa sin intermediarios abstractos
    @GetMapping("/{id}")
    UserResponseDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}