package com.logistica.ms_logistics.service.impl;

import com.logistica.ms_logistics.client.FleetClient;
import com.logistica.ms_logistics.client.WorkOrdersClient;
import com.logistica.ms_logistics.dto.AsignacionRequestDTO;
import com.logistica.ms_logistics.dto.AsignacionResponseDTO;
import com.logistica.ms_logistics.dto.CambioEstadoRequestDTO;
import com.logistica.ms_logistics.dto.DistanciaResponseDTO;
import com.logistica.ms_logistics.dto.feign.OrdenTrabajoFeignDTO;
import com.logistica.ms_logistics.exception.entity.EntityBadRequestException;
import com.logistica.ms_logistics.exception.entity.EntityConflictException;
import com.logistica.ms_logistics.exception.entity.EntityNotFoundException;
import com.logistica.ms_logistics.model.AsignacionLogistica;
import com.logistica.ms_logistics.model.EstadoLogistica;
import com.logistica.ms_logistics.repository.AsignacionLogisticaRepository;
import com.logistica.ms_logistics.service.AsignacionLogisticaService;
import com.logistica.ms_logistics.util.HaversineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AsignacionLogisticaServiceImpl implements AsignacionLogisticaService {

    private static final Logger log = LoggerFactory.getLogger(AsignacionLogisticaServiceImpl.class);

    private final AsignacionLogisticaRepository repository;
    private final WorkOrdersClient workOrdersClient;
    private final FleetClient fleetClient;
    private final AsignacionLogisticaService self;

    public AsignacionLogisticaServiceImpl(AsignacionLogisticaRepository repository,
            WorkOrdersClient workOrdersClient,
            FleetClient fleetClient,
            @Lazy AsignacionLogisticaService self) {
        this.repository = repository;
        this.workOrdersClient = workOrdersClient;
        this.fleetClient = fleetClient;
        this.self = self;
    }

    @Override
    public AsignacionResponseDTO crearAsignacion(AsignacionRequestDTO dto) {
        log.info("[ms-logistics] Creando asignación para ordenTrabajoId={}", dto.getOrdenTrabajoId());

        OrdenTrabajoFeignDTO orden;
        try {
            orden = workOrdersClient.obtenerOrden(dto.getOrdenTrabajoId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "La orden de trabajo con ID " + dto.getOrdenTrabajoId() + " no existe.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-workorders: " + e.getMessage());
        }

        if (!"ASSIGNED".equals(orden.getEstado())) {
            throw new EntityBadRequestException(
                    "La orden debe estar en estado ASSIGNED para asignar logística. Estado actual: "
                            + orden.getEstado());
        }

        if (repository.existsByOrdenTrabajoIdAndEstadoNot(dto.getOrdenTrabajoId(), EstadoLogistica.CANCELADO)) {
            throw new EntityConflictException(
                    "Ya existe una asignación logística activa para la orden " + dto.getOrdenTrabajoId());
        }

        try {
            fleetClient.obtenerVehiculo(dto.getVehiculoId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "El vehículo con ID " + dto.getVehiculoId() + " no existe.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-fleet: " + e.getMessage());
        }

        double distanciaKm = HaversineUtil.calcularDistanciaKm(
                dto.getLatitudOrigen(), dto.getLongitudOrigen(),
                dto.getLatitudDestino(), dto.getLongitudDestino());
        int tiempoMinutos = HaversineUtil.estimarTiempoMinutos(distanciaKm);

        AsignacionLogistica asignacion = new AsignacionLogistica();
        asignacion.setOrdenTrabajoId(dto.getOrdenTrabajoId());
        asignacion.setVehiculoId(dto.getVehiculoId());
        asignacion.setTecnicoId(orden.getTecnicoId());
        asignacion.setLatitudOrigen(dto.getLatitudOrigen());
        asignacion.setLongitudOrigen(dto.getLongitudOrigen());
        asignacion.setLatitudDestino(dto.getLatitudDestino());
        asignacion.setLongitudDestino(dto.getLongitudDestino());
        asignacion.setDistanciaKm(distanciaKm);
        asignacion.setTiempoEstimadoMinutos(tiempoMinutos);
        asignacion.setFechaSalida(dto.getFechaSalida());

        return self.guardarTransaccional(asignacion);
    }

    @Override
    public AsignacionResponseDTO enRuta(Long id) {
        log.info("[ms-logistics] Marcando EN_RUTA asignación={}", id);

        AsignacionLogistica asignacion = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Asignación logística no encontrada con ID: " + id));

        if (asignacion.getEstado() != EstadoLogistica.PLANIFICADO) {
            throw new EntityBadRequestException(
                    "La asignación debe estar en estado PLANIFICADO para pasar a EN_RUTA. Estado actual: "
                            + asignacion.getEstado());
        }

        asignacion.setEstado(EstadoLogistica.EN_RUTA);
        asignacion.setFechaSalida(LocalDateTime.now());

        return self.guardarTransaccional(asignacion);
    }

    @Override
    public AsignacionResponseDTO completar(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-logistics] Completando asignación={}", id);

        AsignacionLogistica asignacion = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Asignación logística no encontrada con ID: " + id));

        if (asignacion.getEstado() != EstadoLogistica.EN_RUTA) {
            throw new EntityBadRequestException(
                    "La asignación debe estar en estado EN_RUTA para completar. Estado actual: "
                            + asignacion.getEstado());
        }

        asignacion.setEstado(EstadoLogistica.COMPLETADO);
        asignacion.setFechaLlegada(LocalDateTime.now());
        asignacion.setObservaciones(dto.getObservaciones());

        return self.guardarTransaccional(asignacion);
    }

    @Override
    public AsignacionResponseDTO cancelar(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-logistics] Cancelando asignación={}", id);

        AsignacionLogistica asignacion = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Asignación logística no encontrada con ID: " + id));

        if (asignacion.getEstado() == EstadoLogistica.COMPLETADO) {
            throw new EntityConflictException(
                    "No se puede cancelar una asignación ya COMPLETADO.");
        }

        asignacion.setEstado(EstadoLogistica.CANCELADO);
        asignacion.setObservaciones(dto.getObservaciones());

        return self.guardarTransaccional(asignacion);
    }

    @Override
    public DistanciaResponseDTO calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
        double distanciaKm = HaversineUtil.calcularDistanciaKm(lat1, lon1, lat2, lon2);
        int tiempoMinutos = HaversineUtil.estimarTiempoMinutos(distanciaKm);
        return new DistanciaResponseDTO(distanciaKm, tiempoMinutos);
    }

    @Override
    public List<AsignacionResponseDTO> listarTodas() {
        return repository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public AsignacionResponseDTO obtenerPorId(Long id) {
        AsignacionLogistica asignacion = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Asignación logística no encontrada con ID: " + id));
        return mapToResponseDTO(asignacion);
    }

    @Override
    public List<AsignacionResponseDTO> listarPorOrden(Long ordenTrabajoId) {
        return repository.findByOrdenTrabajoId(ordenTrabajoId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AsignacionResponseDTO> listarPorVehiculo(Long vehiculoId) {
        return repository.findByVehiculoId(vehiculoId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AsignacionResponseDTO> listarPorTecnico(Long tecnicoId) {
        return repository.findByTecnicoId(tecnicoId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AsignacionResponseDTO> listarPorEstado(EstadoLogistica estado) {
        return repository.findByEstado(estado)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AsignacionResponseDTO guardarTransaccional(AsignacionLogistica asignacion) {
        return mapToResponseDTO(repository.save(asignacion));
    }

    private AsignacionResponseDTO mapToResponseDTO(AsignacionLogistica a) {
        return new AsignacionResponseDTO(
                a.getId(), a.getOrdenTrabajoId(), a.getVehiculoId(), a.getTecnicoId(),
                a.getLatitudOrigen(), a.getLongitudOrigen(),
                a.getLatitudDestino(), a.getLongitudDestino(),
                a.getDistanciaKm(), a.getTiempoEstimadoMinutos(),
                a.getEstado(), a.getFechaCreacion(),
                a.getFechaSalida(), a.getFechaLlegada(), a.getObservaciones());
    }
}
