package com.logistica.ms_security.dto;

import lombok.Data;

@Data
public class RoleResponseDTO {
    private Long id;
    private String rolName;
    private String jsonPermissions;
}