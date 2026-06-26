package com.logistica.ms_fleet.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_fleet.dto.GpsLocationDTO;
import com.logistica.ms_fleet.dto.VehiculoRequestDTO;
import com.logistica.ms_fleet.dto.VehiculoResponseDTO;
import com.logistica.ms_fleet.exception.entity.EntityConflictException;
import com.logistica.ms_fleet.exception.entity.EntityNotFoundException;
import com.logistica.ms_fleet.kafka.FleetKafkaProducer;
import com.logistica.ms_fleet.model.Vehiculo;
import com.logistica.ms_fleet.repository.VehiculoRepository;
import com.logistica.ms_fleet.service.VehiculoService;

@Service
@Transactional(readOnly = true)
public class VehiculoServiceImpl implements VehiculoService {

    private static final Logger log = LoggerFactory.getLogger(VehiculoServiceImpl.class);

    private final VehiculoRepository vehiculoRepository;
    private final FleetKafkaProducer kafkaProducer;

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository, FleetKafkaProducer kafkaProducer) {
        this.vehiculoRepository = vehiculoRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    @Transactional
    public VehiculoResponseDTO crearVehiculo(VehiculoRequestDTO dto) {
        if (vehiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new EntityConflictException("Ya existe un vehículo con la placa: " + dto.getPlaca());
        }
        Vehiculo guardado = vehiculoRepository.save(mapToEntity(dto));
        log.info("[ms-fleet] Vehículo creado id={} placa={}", guardado.getId(), guardado.getPlaca());
        return mapToResponseDTO(guardado);
    }

    @Override
    public List<VehiculoResponseDTO> listarVehiculos() {
        List<VehiculoResponseDTO> lista = vehiculoRepository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        log.info("[ms-fleet] Consulta todos los vehículos. Total: {}", lista.size());
        return lista;
    }

    @Override
    public VehiculoResponseDTO obtenerVehiculoPorId(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ms-fleet] Vehículo no encontrado id={}", id);
                    return new EntityNotFoundException("Vehículo no encontrado con ID: " + id);
                });
        return mapToResponseDTO(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponseDTO actualizarVehiculo(Long id, VehiculoRequestDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se puede actualizar. Vehículo con ID " + id + " no existe."));

        if (vehiculoRepository.existsByPlacaAndIdNot(dto.getPlaca(), id)) {
            throw new EntityConflictException("Ya existe otro vehículo con la placa: " + dto.getPlaca());
        }

        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setCapacidad(dto.getCapacidad());
        if (dto.getEstado() != null) {
            vehiculo.setEstado(dto.getEstado());
        }

        log.info("[ms-fleet] Vehículo actualizado id={}", id);
        return mapToResponseDTO(vehiculo);
    }

    @Override
    @Transactional
    public void eliminarVehiculo(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "No se puede eliminar. Vehículo con ID " + id + " no existe.");
        }
        vehiculoRepository.deleteById(id);
        log.info("[ms-fleet] Vehículo eliminado id={}", id);
    }

    @Override
    public void registrarUbicacion(Long id, GpsLocationDTO dto) {
        if (!vehiculoRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehículo no encontrado con ID: " + id);
        }
        kafkaProducer.publicarUbicacion(id, dto);
        log.info("[ms-fleet] Ubicación GPS publicada para vehículo id={} lat={} lng={}",
                id, dto.getLat(), dto.getLng());
    }

    private Vehiculo mapToEntity(VehiculoRequestDTO dto) {
        Vehiculo v = new Vehiculo();
        v.setPlaca(dto.getPlaca());
        v.setMarca(dto.getMarca());
        v.setModelo(dto.getModelo());
        v.setAnio(dto.getAnio());
        v.setCapacidad(dto.getCapacidad());
        v.setEstado(dto.getEstado());
        return v;
    }

    private VehiculoResponseDTO mapToResponseDTO(Vehiculo v) {
        return new VehiculoResponseDTO(
                v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                v.getAnio(), v.getCapacidad(), v.getEstado(), v.getCreatedAt());
    }
}
