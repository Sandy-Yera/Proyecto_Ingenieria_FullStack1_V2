package com.logistica.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarUsernameDTO {

    @NotBlank(message = "El nuevo correo/username es obligatorio")
    @Email(message = "Debe tener formato de correo electrónico")
    private String username;
}