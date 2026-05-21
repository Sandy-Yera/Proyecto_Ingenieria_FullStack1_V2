package com.logistica.ms_security.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🟢 Corrección: Import oficial de Spring Framework

import com.logistica.ms_security.exception.entity.EntityBadRequestException;
import com.logistica.ms_security.exception.entity.EntityConflictException;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.Role;
import com.logistica.ms_security.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 🟢 Configura lectura optimizada por defecto para todo el servicio
public class RoleService {
    private final RoleRepository roleRepository;

    // CRUD 

    // CREAR
    @Transactional // 🟢 Sobrescribe el modo readOnly para permitir escritura segura
    public Role crearRole(Role role) {
        if (role.getId() != null) {
            throw new EntityConflictException("Ya existe un rol con este ID");
        }
        return roleRepository.save(role);
    }

    // LEER
    public List<Role> listarRole() {
        return roleRepository.findAll();
    }
    
    // ACTUALIZAR
    @Transactional // 🟢 Permite la sincronización de estados con la BD mediante Dirty Checking
    public Role actualizarRole(Long id, Role role) {
        Role roleExistente = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. El rol con ID " + id + " no existe."));

        if (role.getId() != null && !role.getId().equals(id)) {
            throw new EntityBadRequestException("El id ingresado y el del ROL no coinciden");
        }
                
        // 🟢 SOLUCIÓN BAJO 1: Limpieza de asignación redundante.
        // Se removió 'role.setId(id)' ya que modificaba un objeto transitorio. 
        // La actualización real se propaga automáticamente sobre 'roleExistente' gracias al Dirty Checking de JPA.
        roleExistente.setRolName(role.getRolName());
        if (role.getJsonPermissions() != null) {
            roleExistente.setJsonPermissions(role.getJsonPermissions());
        }

        return roleExistente;
    }

    // ELIMINAR
    @Transactional // 🟢 Sobrescribe para permitir borrado físico seguro
    public void eliminarRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el role a eliminar.");
        }

        roleRepository.deleteById(id);
    }
}