package com.logistica.ms_staff.service;

import java.util.List;

import com.logistica.ms_staff.dto.StaffRequestDTO;
import com.logistica.ms_staff.dto.StaffResponseDTO;
import com.logistica.ms_staff.model.Especialidad;

public interface StaffService {

    StaffResponseDTO crearTecnico(StaffRequestDTO dto);

    StaffResponseDTO obtenerTecnicoPorId(Long id);

    List<StaffResponseDTO> obtenerTodosTecnicos();

    List<StaffResponseDTO> obtenerTecnicosPorEspecialidad(Especialidad especialidad);

    List<StaffResponseDTO> obtenerTecnicosDisponibles();

    List<StaffResponseDTO> obtenerTecnicosDisponiblesPorEspecialidad(Especialidad especialidad);
 
    StaffResponseDTO actualizarTecnico(Long id, StaffRequestDTO dto);

    StaffResponseDTO actualizarDisponibilidad(Long id, Boolean disponibilidad);

    void eliminarTecnico(Long id);
}