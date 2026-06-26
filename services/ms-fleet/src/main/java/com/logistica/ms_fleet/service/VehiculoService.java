package com.logistica.ms_fleet.service;

import java.util.List;

import com.logistica.ms_fleet.dto.GpsLocationDTO;
import com.logistica.ms_fleet.dto.VehiculoRequestDTO;
import com.logistica.ms_fleet.dto.VehiculoResponseDTO;

public interface VehiculoService {
    VehiculoResponseDTO crearVehiculo(VehiculoRequestDTO dto);
    List<VehiculoResponseDTO> listarVehiculos();
    VehiculoResponseDTO obtenerVehiculoPorId(Long id);
    VehiculoResponseDTO actualizarVehiculo(Long id, VehiculoRequestDTO dto);
    void eliminarVehiculo(Long id);
    void registrarUbicacion(Long id, GpsLocationDTO dto);
}
