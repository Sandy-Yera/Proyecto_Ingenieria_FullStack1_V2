package com.logistica.ms_workorders.service;

import com.logistica.ms_workorders.dto.AsignarTecnicoRequestDTO;
import com.logistica.ms_workorders.dto.CambioEstadoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoResponseDTO;
import com.logistica.ms_workorders.model.EstadoOrden;
import com.logistica.ms_workorders.model.OrdenTrabajo;

import java.util.List;

public interface OrdenTrabajoService {
    OrdenTrabajoResponseDTO crearOrden(OrdenTrabajoRequestDTO dto);
    OrdenTrabajoResponseDTO guardarOrdenTransaccional(OrdenTrabajo orden);
    OrdenTrabajoResponseDTO asignarTecnico(Long id, AsignarTecnicoRequestDTO dto);
    OrdenTrabajoResponseDTO iniciarTrabajo(Long id);
    OrdenTrabajoResponseDTO completarTrabajo(Long id, CambioEstadoRequestDTO dto);
    OrdenTrabajoResponseDTO cancelarOrden(Long id, CambioEstadoRequestDTO dto);
    void eliminarOrden(Long id);
    List<OrdenTrabajoResponseDTO> listarTodas();
    OrdenTrabajoResponseDTO obtenerPorId(Long id);
    List<OrdenTrabajoResponseDTO> listarPorEstado(EstadoOrden estado);
    List<OrdenTrabajoResponseDTO> listarPorBuilding(Long buildingId);
    List<OrdenTrabajoResponseDTO> listarPorTecnico(Long tecnicoId);
}