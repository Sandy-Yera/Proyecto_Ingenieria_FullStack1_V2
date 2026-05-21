package com.logistica.ms_security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.logistica.ms_security.model.RoleAssignment;

@Repository
public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {

    /**
     * 🟢 PROTECCIÓN DE RELACIÓN DE TABLAS:
     * Busca si ya existe una asignación específica para un usuario y un rol.
     * Esto evita que un mismo usuario tenga el mismo rol duplicado en la base de datos.
     */
    Optional<RoleAssignment> findByIdUserAndIdRole(Long idUser, Long idRole);
}