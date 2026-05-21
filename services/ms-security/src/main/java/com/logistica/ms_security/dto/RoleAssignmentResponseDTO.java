package com.logistica.ms_security.dto;

import lombok.Data;

@Data
public class RoleAssignmentResponseDTO {
    private Long id;
    private Long idRole;
    private Long idUser;
}