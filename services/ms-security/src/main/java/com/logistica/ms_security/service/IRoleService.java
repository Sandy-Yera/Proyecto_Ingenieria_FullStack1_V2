package com.logistica.ms_security.service;

import java.util.List;

import com.logistica.ms_security.dto.RoleRequestDTO;
import com.logistica.ms_security.dto.RoleResponseDTO;

public interface IRoleService {
    RoleResponseDTO crearRole(RoleRequestDTO roleDTO);

    List<RoleResponseDTO> listarRole();

    RoleResponseDTO actualizarRole(Long id, RoleRequestDTO roleDTO);

    void eliminarRole(Long id);
}
