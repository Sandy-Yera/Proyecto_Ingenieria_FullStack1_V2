package com.logistica.ms_workorders.service.impl;

import com.logistica.ms_workorders.client.BuildingsClient;
import com.logistica.ms_workorders.client.QuotesClient;
import com.logistica.ms_workorders.client.ScheduleClient;
import com.logistica.ms_workorders.client.UsersClient;
import com.logistica.ms_workorders.dto.AsignarTecnicoRequestDTO;
import com.logistica.ms_workorders.dto.CambioEstadoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoResponseDTO;
import com.logistica.ms_workorders.dto.feign.BloqueFeignRequestDTO;
import com.logistica.ms_workorders.exception.entity.EntityBadRequestException;
import com.logistica.ms_workorders.exception.entity.EntityNotFoundException;
import com.logistica.ms_workorders.model.EstadoOrden;
import com.logistica.ms_workorders.model.OrdenTrabajo;
import com.logistica.ms_workorders.repository.OrdenTrabajoRepository;
import com.logistica.ms_workorders.service.OrdenTrabajoService;
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
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private static final Logger log = LoggerFactory.getLogger(OrdenTrabajoServiceImpl.class);

    private final OrdenTrabajoRepository repository;
    private final UsersClient usersClient;
    private final BuildingsClient buildingsClient;
    private final QuotesClient quotesClient;
    private final ScheduleClient scheduleClient;
    private final OrdenTrabajoService self;

    public OrdenTrabajoServiceImpl(OrdenTrabajoRepository repository,
            UsersClient usersClient,
            BuildingsClient buildingsClient,
            QuotesClient quotesClient,
            ScheduleClient scheduleClient,
            @Lazy OrdenTrabajoService self) {
        this.repository = repository;
        this.usersClient = usersClient;
        this.buildingsClient = buildingsClient;
        this.quotesClient = quotesClient;
        this.scheduleClient = scheduleClient;
        this.self = self;
    }

    @Override
    public OrdenTrabajoResponseDTO crearOrden(OrdenTrabajoRequestDTO dto) {
        log.info("[ms-workorders] Creando orden buildingId={}", dto.getBuildingId());

        try {
            buildingsClient.obtenerEdificioPorId(dto.getBuildingId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "El edificio con ID " + dto.getBuildingId() + " no existe.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-buildings: " + e.getMessage());
        }

        if (dto.getQuoteId() != null) {
            try {
                quotesClient.obtenerCotizacionPorId(dto.getQuoteId());
            } catch (feign.FeignException.NotFound e) {
                throw new EntityNotFoundException(
                        "La cotización con ID " + dto.getQuoteId() + " no existe.");
            } catch (feign.FeignException e) {
                throw new EntityBadRequestException(
                        "Error de comunicación con ms-quotes: " + e.getMessage());
            }
        }

        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setBuildingId(dto.getBuildingId());
        orden.setQuoteId(dto.getQuoteId());
        orden.setDescripcion(dto.getDescripcion());
        orden.setCategoria(dto.getCategoria());

        return self.guardarOrdenTransaccional(orden);
    }

    @Override
    public OrdenTrabajoResponseDTO asignarTecnico(Long id, AsignarTecnicoRequestDTO dto) {
        log.info("[ms-workorders] Asignando tecnico={} a orden={}", dto.getTecnicoId(), id);

        OrdenTrabajo orden = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Orden de trabajo no encontrada con ID: " + id));

        if (orden.getEstado() != EstadoOrden.PENDING) {
            throw new EntityBadRequestException(
                    "La orden debe estar en estado PENDING para asignar un técnico. Estado actual: "
                            + orden.getEstado());
        }

        try {
            usersClient.obtenerUsuarioPorId(dto.getTecnicoId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "El técnico con ID " + dto.getTecnicoId() + " no existe.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-users: " + e.getMessage());
        }

        try {
            scheduleClient.crearBloque(new BloqueFeignRequestDTO(
                    dto.getTecnicoId(), dto.getFechaInicio(), dto.getFechaFin()));
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "No se pudo crear el bloque horario: " + e.getMessage());
        }

        orden.setTecnicoId(dto.getTecnicoId());
        orden.setEstado(EstadoOrden.ASSIGNED);
        orden.setFechaAsignacion(LocalDateTime.now());

        return self.guardarOrdenTransaccional(orden);
    }

    @Override
    public OrdenTrabajoResponseDTO iniciarTrabajo(Long id) {
        log.info("[ms-workorders] Iniciando trabajo orden={}", id);

        OrdenTrabajo orden = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Orden de trabajo no encontrada con ID: " + id));

        if (orden.getEstado() != EstadoOrden.ASSIGNED) {
            throw new EntityBadRequestException(
                    "La orden debe estar en estado ASSIGNED para iniciar. Estado actual: "
                            + orden.getEstado());
        }

        orden.setEstado(EstadoOrden.IN_PROGRESS);
        orden.setFechaInicio(LocalDateTime.now());

        return self.guardarOrdenTransaccional(orden);
    }

    @Override
    public OrdenTrabajoResponseDTO completarTrabajo(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-workorders] Completando orden={}", id);

        OrdenTrabajo orden = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Orden de trabajo no encontrada con ID: " + id));

        if (orden.getEstado() != EstadoOrden.IN_PROGRESS) {
            throw new EntityBadRequestException(
                    "La orden debe estar en estado IN_PROGRESS para completar. Estado actual: "
                            + orden.getEstado());
        }

        orden.setEstado(EstadoOrden.COMPLETED);
        orden.setFechaFin(LocalDateTime.now());
        orden.setObservaciones(dto.getObservaciones());

        return self.guardarOrdenTransaccional(orden);
    }

    @Override
    public OrdenTrabajoResponseDTO cancelarOrden(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-workorders] Cancelando orden={}", id);

        OrdenTrabajo orden = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Orden de trabajo no encontrada con ID: " + id));

        if (orden.getEstado() == EstadoOrden.COMPLETED) {
            throw new EntityBadRequestException(
                    "No se puede cancelar una orden ya COMPLETED.");
        }

        orden.setEstado(EstadoOrden.CANCELLED);
        orden.setObservaciones(dto.getObservaciones());

        return self.guardarOrdenTransaccional(orden);
    }

    @Override
    @Transactional
    public void eliminarOrden(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "No se puede eliminar. Orden con ID " + id + " no existe.");
        }
        repository.deleteById(id);
        log.info("[ms-workorders] Orden eliminada id={}", id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrdenTrabajoResponseDTO guardarOrdenTransaccional(OrdenTrabajo orden) {
        OrdenTrabajo guardada = repository.save(orden);
        return mapToResponseDTO(guardada);
    }

    @Override
    public List<OrdenTrabajoResponseDTO> listarTodas() {
        return repository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public OrdenTrabajoResponseDTO obtenerPorId(Long id) {
        OrdenTrabajo orden = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Orden de trabajo no encontrada con ID: " + id));
        return mapToResponseDTO(orden);
    }

    @Override
    public List<OrdenTrabajoResponseDTO> listarPorEstado(EstadoOrden estado) {
        return repository.findByEstado(estado)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrdenTrabajoResponseDTO> listarPorBuilding(Long buildingId) {
        return repository.findByBuildingId(buildingId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrdenTrabajoResponseDTO> listarPorTecnico(Long tecnicoId) {
        return repository.findByTecnicoId(tecnicoId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    private OrdenTrabajoResponseDTO mapToResponseDTO(OrdenTrabajo o) {
        return new OrdenTrabajoResponseDTO(
                o.getId(), o.getBuildingId(), o.getQuoteId(), o.getTecnicoId(),
                o.getEstado(), o.getDescripcion(), o.getCategoria(),
                o.getFechaCreacion(), o.getFechaAsignacion(),
                o.getFechaInicio(), o.getFechaFin(), o.getObservaciones());
    }
}