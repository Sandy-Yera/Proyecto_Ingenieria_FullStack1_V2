package com.logistica.ms_quotes.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_quotes.client.BuildingClient;
import com.logistica.ms_quotes.client.UserClient;
import com.logistica.ms_quotes.dto.CotizacionRequestDTO;
import com.logistica.ms_quotes.dto.CotizacionResponseDTO;
import com.logistica.ms_quotes.exception.entity.EntityBadRequestException;
import com.logistica.ms_quotes.exception.entity.EntityConflictException;
import com.logistica.ms_quotes.exception.entity.EntityNotFoundException;
import com.logistica.ms_quotes.model.Cotizacion;
import com.logistica.ms_quotes.model.Status;
import com.logistica.ms_quotes.repository.CotizacionRepository;
import com.logistica.ms_quotes.service.CotizacionService;

/**
 * IMPLEMENTACIÓN DEL SERVICIO — CotizacionServiceImpl
 * Contiene toda la lógica de negocio del dominio de Cotizaciones.
 *
 * La creación se divide en dos pasos para evitar retener conexiones del pool
 * Hikari mientras se esperan las validaciones remotas vía Feign:
 *  1. crearCotizacion (fuera de transacción): valida Usuario y Edificio remotos.
 *  2. guardarCotizacionTransaccional (a través del proxy self): persiste de forma atómica.
 */
@Service
@Transactional(readOnly = true)
public class CotizacionServiceImpl implements CotizacionService {

    private static final Logger log = LoggerFactory.getLogger(CotizacionServiceImpl.class);

    private final CotizacionRepository cotizacionRepository;
    private final UserClient userClient;
    private final BuildingClient buildingClient;
    private final CotizacionService self;

    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository,
                                  UserClient userClient,
                                  BuildingClient buildingClient,
                                  @Lazy CotizacionService self) {
        this.cotizacionRepository = cotizacionRepository;
        this.userClient = userClient;
        this.buildingClient = buildingClient;
        this.self = self;
    }

    // ============================================================
    //  CREAR (Paso 1: fuera de la transacción — validaciones remotas)
    // ============================================================
    @Override
    public CotizacionResponseDTO crearCotizacion(CotizacionRequestDTO dto) {
        log.info("[ms-quotes] Iniciando creación de cotización para userId={} buildingId={}",
                dto.getUserId(), dto.getBuildingId());

        // 1. Validación remota del Usuario en ms-users via OpenFeign con excepciones tipificadas
        try {
            userClient.obtenerUsuarioPorId(dto.getUserId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException("No se puede crear la cotización. El Usuario con ID "
                    + dto.getUserId() + " no existe en el sistema.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException("Error de comunicación remota con ms-users: " + e.getMessage());
        }

        // 2. Validación remota del Edificio en ms-buildings via OpenFeign con excepciones tipificadas
        try {
            buildingClient.obtenerEdificioPorId(dto.getBuildingId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException("No se puede crear la cotización. El Edificio con ID "
                    + dto.getBuildingId() + " no existe en el sistema.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException("Error de comunicación remota con ms-buildings: " + e.getMessage());
        }

        // 3. Pasamos al proxy para ejecutar la persistencia atómica rápida
        return self.guardarCotizacionTransaccional(mapToEntity(dto));
    }

    // ============================================================
    //  CREAR (Paso 2: persistencia aislada y transaccional)
    // ============================================================
    @Override
    @Transactional
    public CotizacionResponseDTO guardarCotizacionTransaccional(Cotizacion cotizacion) {
        if (cotizacion.getId() != null && cotizacionRepository.existsById(cotizacion.getId())) {
            throw new EntityConflictException("Ya existe una cotización con este ID");
        }
        Cotizacion guardada = cotizacionRepository.save(cotizacion);
        log.info("[ms-quotes] Cotización creada exitosamente con id={} status={}",
                guardada.getId(), guardada.getStatus());
        return mapToResponseDTO(guardada);
    }

    // ============================================================
    //  LEER — Todos
    // ============================================================
    @Override
    public List<CotizacionResponseDTO> listarCotizaciones() {
        List<CotizacionResponseDTO> lista = cotizacionRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        log.info("[ms-quotes] Consulta de todas las cotizaciones. Total: {}", lista.size());
        return lista;
    }

    // ============================================================
    //  LEER — Por ID
    // ============================================================
    @Override
    public CotizacionResponseDTO obtenerCotizacionPorId(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ms-quotes] Cotización no encontrada con id={}", id);
                    return new EntityNotFoundException("Cotización no encontrada con ID: " + id);
                });

        return mapToResponseDTO(cotizacion);
    }

    // ============================================================
    //  LEER — Por Usuario
    // ============================================================
    @Override
    public List<CotizacionResponseDTO> listarPorUsuario(Long userId) {
        List<CotizacionResponseDTO> lista = cotizacionRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        log.info("[ms-quotes] Consulta cotizaciones por userId={}. Total: {}", userId, lista.size());
        return lista;
    }

    // ============================================================
    //  LEER — Por Estado
    // ============================================================
    @Override
    public List<CotizacionResponseDTO> listarPorEstado(Status status) {
        List<CotizacionResponseDTO> lista = cotizacionRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        log.info("[ms-quotes] Consulta cotizaciones por status={}. Total: {}", status, lista.size());
        return lista;
    }

    // ============================================================
    //  ACTUALIZAR
    // ============================================================
    @Override
    @Transactional
    public CotizacionResponseDTO actualizarCotizacion(Long id, CotizacionRequestDTO dto) {
        Cotizacion cotizacionExistente = cotizacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ms-quotes] Intento de actualizar cotización inexistente con id={}", id);
                    return new EntityNotFoundException(
                            "No se puede actualizar. La cotización con ID " + id + " no existe.");
                });

        // No se puede reactivar una cotización que ya fue rechazada
        if (dto.getStatus() != null && dto.getStatus() == Status.ACCEPTED
                && cotizacionExistente.getStatus() == Status.REJECTED) {
            log.warn("[ms-quotes] Intento de reactivar cotización rechazada id={}", id);
            throw new EntityConflictException(
                    "No se puede aceptar una cotización que ya fue rechazada (ID: " + id + ")");
        }

        // Sincronización mediante Dirty Checking usando los datos del Request DTO
        cotizacionExistente.setUserId(dto.getUserId());
        cotizacionExistente.setBuildingId(dto.getBuildingId());
        cotizacionExistente.setDescription(dto.getDescription());
        cotizacionExistente.setCategory(dto.getCategory());
        cotizacionExistente.setEstimatedAmount(dto.getEstimatedAmount());

        if (dto.getStatus() != null) {
            cotizacionExistente.setStatus(dto.getStatus());
        }

        log.info("[ms-quotes] Cotización actualizada correctamente id={}", id);
        return mapToResponseDTO(cotizacionExistente);
    }

    // ============================================================
    //  ELIMINAR
    // ============================================================
    @Override
    @Transactional
    public void eliminarCotizacion(Long id) {
        if (!cotizacionRepository.existsById(id)) {
            log.warn("[ms-quotes] Intento de eliminar cotización inexistente con id={}", id);
            throw new EntityNotFoundException(
                    "No se puede eliminar. La cotización con ID " + id + " no existe.");
        }
        cotizacionRepository.deleteById(id);
        log.info("[ms-quotes] Cotización eliminada correctamente id={}", id);
    }

    // ============================================================
    //  HELPERS DE MAPEO (privados — sin exposición al Controller)
    // ============================================================

    private Cotizacion mapToEntity(CotizacionRequestDTO dto) {
        Cotizacion c = new Cotizacion();
        c.setUserId(dto.getUserId());
        c.setBuildingId(dto.getBuildingId());
        c.setDescription(dto.getDescription());
        c.setCategory(dto.getCategory());
        c.setEstimatedAmount(dto.getEstimatedAmount());
        if (dto.getStatus() != null) {
            c.setStatus(dto.getStatus()); // Si es null, @PrePersist asigna PENDING
        }
        return c;
    }

    private CotizacionResponseDTO mapToResponseDTO(Cotizacion c) {
        return new CotizacionResponseDTO(
                c.getId(),
                c.getUserId(),
                c.getBuildingId(),
                c.getDescription(),
                c.getCategory(),
                c.getEstimatedAmount(),
                c.getStatus(),
                c.getCreatedAt()
        );
    }
}
