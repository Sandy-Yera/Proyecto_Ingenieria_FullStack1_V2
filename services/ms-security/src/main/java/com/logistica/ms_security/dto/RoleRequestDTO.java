package com.logistica.ms_security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String rolName;

    private String jsonPermissions;
}