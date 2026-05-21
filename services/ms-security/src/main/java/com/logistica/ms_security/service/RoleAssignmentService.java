package com.logistica.ms_security.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🟢 Import oficial de Spring

import com.logistica.ms_security.exception.entity.EntityBadRequestException;
import com.logistica.ms_security.exception.entity.EntityConflictException;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.RoleAssignment;
import com.logistica.ms_security.repository.RoleAssignmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 🟢 MEJORA ETAPA 2: Por defecto, todo el servicio optimiza las lecturas en la BD
public class RoleAssignmentService {

    private final RoleAssignmentRepository roleAssignmentRepository;

    @Transactional // 🟢 Sobrescribe para permitir escritura segura
    public RoleAssignment crearRoleAssignment(RoleAssignment assignment) {
        if (assignment.getIdUser() == null || assignment.getIdRole() == null) {
            throw new EntityBadRequestException("El ID de usuario y el ID de rol son requeridos.");
        }

        // 🟢 Corre en modo optimizado de lectura hasta que se ejecute el .save() final
        roleAssignmentRepository.findByIdUserAndIdRole(assignment.getIdUser(), assignment.getIdRole())
                .ifPresent(existing -> {
                    throw new EntityConflictException("El usuario con ID " + assignment.getIdUser() + 
                            " ya tiene asignado el rol con ID " + assignment.getIdRole());
                });

        assignment.setId(null); 
        return roleAssignmentRepository.save(assignment);
    }

    // 🟢 Ya no necesita anotación individual porque hereda el 'readOnly = true' de la clase
    public List<RoleAssignment> listarRoleAssignments() {
        return roleAssignmentRepository.findAll();
    }

    @Transactional // 🟢 Sobrescribe para permitir actualización y sincronización por Dirty Checking
    public RoleAssignment actualizarRoleAssignment(@NonNull Long id, RoleAssignment assignment) {
        RoleAssignment assignmentExistente = roleAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. La asignación con ID " + id + " no existe."));

        if (assignment.getId() != null && !assignment.getId().equals(id)) {
            throw new EntityBadRequestException("El id ingresado y el de la asignación no coinciden.");
        }
        
        // 🟢 Al estar la clase en readOnly por defecto, esta consulta intermedia no bloquea filas de la base de datos
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

    @Transactional // 🟢 Sobrescribe para permitir borrado físico
    public void eliminarRoleAssignment(@NonNull Long id) {
        if (!roleAssignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la asignación a eliminar.");
        }
        roleAssignmentRepository.deleteById(id);
    }
}