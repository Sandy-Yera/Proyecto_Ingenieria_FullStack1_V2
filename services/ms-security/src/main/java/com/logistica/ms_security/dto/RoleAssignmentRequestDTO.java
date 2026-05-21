package com.logistica.ms_security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleAssignmentRequestDTO {

    @NotNull(message = "El id del Rol es obligatorio")
    private Long idRole;

    @NotNull(message = "El id del user es obligatorio")
    private Long idUser;
}