package com.logistica.ms_logistics.service;

import com.logistica.ms_logistics.dto.AsignacionRequestDTO;
import com.logistica.ms_logistics.dto.AsignacionResponseDTO;
import com.logistica.ms_logistics.dto.CambioEstadoRequestDTO;
import com.logistica.ms_logistics.dto.DistanciaResponseDTO;
import com.logistica.ms_logistics.model.AsignacionLogistica;
import com.logistica.ms_logistics.model.EstadoLogistica;

import java.util.List;

public interface AsignacionLogisticaService {

    AsignacionResponseDTO crearAsignacion(AsignacionRequestDTO dto);

    AsignacionResponseDTO enRuta(Long id);

    AsignacionResponseDTO completar(Long id, CambioEstadoRequestDTO dto);

    AsignacionResponseDTO cancelar(Long id, CambioEstadoRequestDTO dto);

    DistanciaResponseDTO calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2);

    List<AsignacionResponseDTO> listarTodas();

    AsignacionResponseDTO obtenerPorId(Long id);

    List<AsignacionResponseDTO> listarPorOrden(Long ordenTrabajoId);

    List<AsignacionResponseDTO> listarPorVehiculo(Long vehiculoId);

    List<AsignacionResponseDTO> listarPorTecnico(Long tecnicoId);

    List<AsignacionResponseDTO> listarPorEstado(EstadoLogistica estado);

    AsignacionResponseDTO guardarTransaccional(AsignacionLogistica asignacion);
}
