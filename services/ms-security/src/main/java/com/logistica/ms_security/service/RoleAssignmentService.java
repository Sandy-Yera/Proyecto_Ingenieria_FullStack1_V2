package com.logistica.ms_security.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.logistica.ms_security.exception.entity.EntityBadRequestException;
import com.logistica.ms_security.exception.entity.EntityConflictException;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.RoleAssignment;
import com.logistica.ms_security.repository.RoleAssignmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleAssignmentService {
    private final RoleAssignmentRepository roleAssignmentRepository;

    @Transactional // Agregado para asegurar la consistencia al consultar y luego insertar
    public RoleAssignment crearRoleAssignment(RoleAssignment assignment) {
        if (assignment.getIdUser() == null || assignment.getIdRole() == null) {
            throw new EntityBadRequestException("El ID de usuario y el ID de rol son requeridos.");
        }

        // 🟢 PREVENCIÓN DE DUPLICADOS: Validamos si ya existe la combinación exacta
        roleAssignmentRepository.findByIdUserAndIdRole(assignment.getIdUser(), assignment.getIdRole())
                .ifPresent(existing -> {
                    throw new EntityConflictException("El usuario con ID " + assignment.getIdUser() + 
                            " ya tiene asignado el rol con ID " + assignment.getIdRole());
                });

        assignment.setId(null); 
        return roleAssignmentRepository.save(assignment);
    }

    public List<RoleAssignment> listarRoleAssignments() {
        return roleAssignmentRepository.findAll();
    }

    @Transactional
    public RoleAssignment actualizarRoleAssignment(@NonNull Long id, RoleAssignment assignment) {
        RoleAssignment assignmentExistente = roleAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. La asignación con ID " + id + " no existe."));

        if (assignment.getId() != null && !assignment.getId().equals(id)) {
            throw new EntityBadRequestException("El id ingresado y el de la asignación no coinciden.");
        }
        
        // 🟢 VALIDACIÓN AL ACTUALIZAR: Evita mutar una asignación hacia valores que ya choquen con otra existente
        if (!assignmentExistente.getIdUser().equals(assignment.getIdUser()) || !assignmentExistente.getIdRole().equals(assignment.getIdRole())) {
            roleAssignmentRepository.findByIdUserAndIdRole(assignment.getIdUser(), assignment.getIdRole())
                    .ifPresent(existing -> {
                        throw new EntityConflictException("No se puede actualizar: Ya existe una asignación para ese usuario y rol.");
                    });
        }
        
        assignmentExistente.setIdRole(assignment.getIdRole());
        assignmentExistente.setIdUser(assignment.getIdUser());

        return assignmentExistente;
    }

    @Transactional
    public void eliminarRoleAssignment(@NonNull Long id) {
        if (!roleAssignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la asignación a eliminar.");
        }
        roleAssignmentRepository.deleteById(id);
    }
}