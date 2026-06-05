package com.logistica.ms_staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.model.Staff;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    // Buscar técnicos por especialidad
    List<Staff> findByEspecialidad(Especialidad especialidad);

    // Buscar técnicos disponibles (consultado por ms-logistics)
    List<Staff> findByEstadoDisponibilidadTrue();

    // Buscar técnicos disponibles por especialidad (asignación inteligente)
    List<Staff> findByEspecialidadAndEstadoDisponibilidadTrue(Especialidad especialidad);

    // Verificar si existe una certificación SEC registrada
    boolean existsByCertificacionSec(String certificacionSec);
}