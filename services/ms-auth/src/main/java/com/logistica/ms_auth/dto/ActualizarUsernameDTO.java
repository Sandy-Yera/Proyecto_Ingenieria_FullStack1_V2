package com.logistica.ms_auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarUsernameDTO {

    @NotBlank(message = "El nuevo username/email es requerido.")
    @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres.")
    private String username;
}