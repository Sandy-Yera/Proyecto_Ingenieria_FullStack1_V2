package com.logistica.ms_security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_security.dto.RoleRequestDTO;  // 🟢 Import DTO Request
import com.logistica.ms_security.dto.RoleResponseDTO; // 🟢 Import DTO Response
import com.logistica.ms_security.exception.entity.EntityBadRequestException;
import com.logistica.ms_security.exception.entity.EntityConflictException;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.Role;
import com.logistica.ms_security.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    
    private final RoleRepository roleRepository;

    // CREAR
    @Transactional
    public RoleResponseDTO crearRole(RoleRequestDTO roleDTO) {
        // Mapear de RequestDTO a Entidad
        Role role = new Role();
        role.setRolName(roleDTO.getRolName());
        role.setJsonPermissions(roleDTO.getJsonPermissions());

        Role roleGuardado = roleRepository.save(role);
        return convertToResponseDTO(roleGuardado);
    }

    // LEER
    public List<RoleResponseDTO> listarRole() {
        return roleRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ACTUALIZAR
    @Transactional
    public RoleResponseDTO actualizarRole(Long id, RoleRequestDTO roleDTO) {
        Role roleExistente = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. El rol con ID " + id + " no existe."));
                
        // Sincronización mediante Dirty Checking mapeando los datos del Request DTO
        roleExistente.setRolName(roleDTO.getRolName());
        if (roleDTO.getJsonPermissions() != null) {
            roleExistente.setJsonPermissions(roleDTO.getJsonPermissions());
        }

        return convertToResponseDTO(roleExistente);
    }

    // ELIMINAR
    @Transactional
    public void eliminarRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el role a eliminar.");
        }
        roleRepository.deleteById(id);
    }

    // 🟢 MÉTODOS AUXILIARES DE CONVERSIÓN (Mappers manuales limpios)
    private RoleResponseDTO convertToResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setRolName(role.getRolName());
        dto.setJsonPermissions(role.getJsonPermissions());
        return dto;
    }
}