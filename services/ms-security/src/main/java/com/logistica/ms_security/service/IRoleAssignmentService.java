package com.logistica.ms_security.service;

import java.util.List;

import com.logistica.ms_security.dto.RoleAssignmentRequestDTO;
import com.logistica.ms_security.dto.RoleAssignmentResponseDTO;

public interface IRoleAssignmentService {
    RoleAssignmentResponseDTO crearRoleAssignment(RoleAssignmentRequestDTO dto);

    List<RoleAssignmentResponseDTO> listarRoleAssignments();

    RoleAssignmentResponseDTO actualizarRoleAssignment(Long id, RoleAssignmentRequestDTO dto);

    void eliminarRoleAssignment(Long id);
}
