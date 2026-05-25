package com.logistica.ms_security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.logistica.ms_security.model.RoleAssignment;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {

    /**
     * 🟢 PROTECCIÓN DE RELACIÓN DE TABLAS:
     * Busca si ya existe una asignación específica para un usuario y un rol.
     * Esto evita que un mismo usuario tenga el mismo rol duplicado en la base de datos.
     */
    Optional<RoleAssignment> findByIdUserAndIdRole(Long idUser, Long idRole);

    // NUEVO: Para la limpieza reactiva por Kafka
    List<RoleAssignment> findAllByIdUser(Long idUser);

    @Modifying
    @Query("DELETE FROM RoleAssignment r WHERE r.idUser = :userId")
    void deleteAllByIdUser(@Param("userId") Long userId);
}