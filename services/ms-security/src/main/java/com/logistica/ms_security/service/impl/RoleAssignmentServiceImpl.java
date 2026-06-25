package com.logistica.ms_security.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_security.dto.RoleAssignmentRequestDTO;  // 🟢 Import Request DTO
import com.logistica.ms_security.dto.RoleAssignmentResponseDTO; // 🟢 Import Response DTO
import com.logistica.ms_security.exception.entity.EntityBadRequestException;
import com.logistica.ms_security.exception.entity.EntityConflictException;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.RoleAssignment;
import com.logistica.ms_security.repository.RoleAssignmentRepository;
import com.logistica.ms_security.service.IRoleAssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 🟢 Mantiene optimización de lectura global de la Etapa 2
public class RoleAssignmentServiceImpl implements IRoleAssignmentService {
    private final RoleAssignmentRepository roleAssignmentRepository;

    @Transactional // 🟢 Transacción de escritura
    public RoleAssignmentResponseDTO crearRoleAssignment(RoleAssignmentRequestDTO dto) {
        if (dto.getIdUser() == null || dto.getIdRole() == null) {
            throw new EntityBadRequestException("El ID de usuario y el ID de rol son requeridos.");
        }

        // Validación de duplicados usando los datos del DTO
        roleAssignmentRepository.findByIdUserAndIdRole(dto.getIdUser(), dto.getIdRole())
                .ifPresent(existing -> {
                    throw new EntityConflictException("El usuario con ID " + dto.getIdUser() + 
                            " ya tiene asignado el rol con ID " + dto.getIdRole());
                });

        // Mapeo manual limpio de Request DTO a Entidad
        RoleAssignment assignment = new RoleAssignment();
        assignment.setIdRole(dto.getIdRole());
        assignment.setIdUser(dto.getIdUser());

        RoleAssignment guardado = roleAssignmentRepository.save(assignment);
        return convertToResponseDTO(guardado);
    }

    // LEER (Optimizado en Solo Lectura)
    public List<RoleAssignmentResponseDTO> listarRoleAssignments() {
        return roleAssignmentRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional // 🟢 Transacción de escritura y Dirty Checking
    public RoleAssignmentResponseDTO actualizarRoleAssignment(@NonNull Long id, RoleAssignmentRequestDTO dto) {
        RoleAssignment assignmentExistente = roleAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. La asignación con ID " + id + " no existe."));
        
        // Validación cruzada para evitar colisiones al mutar
        if (!assignmentExistente.getIdUser().equals(dto.getIdUser()) || !assignmentExistente.getIdRole().equals(dto.getIdRole())) {
            roleAssignmentRepository.findByIdUserAndIdRole(dto.getIdUser(), dto.getIdRole())
                    .ifPresent(existing -> {
                        throw new EntityConflictException("No se puede actualizar: Ya existe una asignación para ese usuario y rol.");
                    });
        }
        
        // Sincronización automática mediante Dirty Checking
        assignmentExistente.setIdRole(dto.getIdRole());
        assignmentExistente.setIdUser(dto.getIdUser());

        return convertToResponseDTO(assignmentExistente);
    }

    @Transactional
    public void eliminarRoleAssignment(@NonNull Long id) {
        if (!roleAssignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la asignación a eliminar.");
        }
        roleAssignmentRepository.deleteById(id);
    }

    // 🟢 MÉTODOS AUXILIARES DE CONVERSIÓN (Mappers manuales limpios)
    private RoleAssignmentResponseDTO convertToResponseDTO(RoleAssignment assignment) {
        RoleAssignmentResponseDTO dto = new RoleAssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setIdRole(assignment.getIdRole());
        dto.setIdUser(assignment.getIdUser());
        return dto;
    }
}
