package com.logistica.ms_billing.service.impl;

import com.logistica.ms_billing.client.QuotesClient;
import com.logistica.ms_billing.client.WorkOrdersClient;
import com.logistica.ms_billing.dto.CambioEstadoRequestDTO;
import com.logistica.ms_billing.dto.FacturaRequestDTO;
import com.logistica.ms_billing.dto.FacturaResponseDTO;
import com.logistica.ms_billing.dto.feign.QuoteFeignDTO;
import com.logistica.ms_billing.dto.feign.WorkOrderFeignDTO;
import com.logistica.ms_billing.exception.entity.EntityBadRequestException;
import com.logistica.ms_billing.exception.entity.EntityConflictException;
import com.logistica.ms_billing.exception.entity.EntityNotFoundException;
import com.logistica.ms_billing.model.EstadoFactura;
import com.logistica.ms_billing.model.Factura;
import com.logistica.ms_billing.repository.FacturaRepository;
import com.logistica.ms_billing.service.FacturaService;
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
public class FacturaServiceImpl implements FacturaService {

    private static final Logger log = LoggerFactory.getLogger(FacturaServiceImpl.class);

    private final FacturaRepository repository;
    private final WorkOrdersClient workOrdersClient;
    private final QuotesClient quotesClient;
    private final FacturaService self;

    public FacturaServiceImpl(FacturaRepository repository,
            WorkOrdersClient workOrdersClient,
            QuotesClient quotesClient,
            @Lazy FacturaService self) {
        this.repository = repository;
        this.workOrdersClient = workOrdersClient;
        this.quotesClient = quotesClient;
        this.self = self;
    }

    @Override
    public FacturaResponseDTO emitirFactura(FacturaRequestDTO dto) {
        log.info("[ms-billing] Emitiendo factura para workOrderId={}", dto.getWorkOrderId());

        WorkOrderFeignDTO orden;
        try {
            orden = workOrdersClient.obtenerOrden(dto.getWorkOrderId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "Orden de trabajo con ID " + dto.getWorkOrderId() + " no existe.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-workorders: " + e.getMessage());
        }

        if (!orden.getEstado().equals("COMPLETED")) {
            throw new EntityBadRequestException(
                    "Solo se puede facturar una orden en estado COMPLETED. Estado actual: "
                            + orden.getEstado());
        }

        repository.findByWorkOrderId(dto.getWorkOrderId()).ifPresent(f -> {
            throw new EntityConflictException(
                    "Ya existe una factura para la orden de trabajo ID " + dto.getWorkOrderId());
        });

        Double monto;
        if (dto.getMontoManual() != null) {
            monto = dto.getMontoManual();
        } else if (orden.getQuoteId() != null) {
            try {
                QuoteFeignDTO quote = quotesClient.obtenerCotizacion(orden.getQuoteId());
                monto = quote.getEstimatedAmount();
            } catch (feign.FeignException e) {
                throw new EntityBadRequestException(
                        "Error al obtener cotización ID " + orden.getQuoteId() + ": " + e.getMessage());
            }
        } else {
            throw new EntityBadRequestException(
                    "No se puede determinar el monto: la orden no tiene cotización y no se proporcionó montoManual.");
        }

        Factura factura = new Factura();
        factura.setWorkOrderId(dto.getWorkOrderId());
        factura.setTecnicoId(orden.getTecnicoId());
        factura.setBuildingId(orden.getBuildingId());
        factura.setDescripcion(orden.getDescripcion());
        factura.setMontoTotal(monto);

        return self.guardarTransaccional(factura);
    }

    @Override
    public FacturaResponseDTO pagarFactura(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-billing] Pagando factura id={}", id);

        Factura factura = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Factura no encontrada con ID: " + id));

        if (factura.getEstado() != EstadoFactura.PENDIENTE) {
            throw new EntityConflictException(
                    "Solo se puede pagar una factura en estado PENDIENTE. Estado actual: "
                            + factura.getEstado());
        }

        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(LocalDateTime.now());
        factura.setObservaciones(dto.getObservaciones());

        return self.guardarTransaccional(factura);
    }

    @Override
    public FacturaResponseDTO anularFactura(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-billing] Anulando factura id={}", id);

        Factura factura = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Factura no encontrada con ID: " + id));

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new EntityConflictException(
                    "No se puede anular una factura ya pagada.");
        }

        factura.setEstado(EstadoFactura.ANULADA);
        factura.setObservaciones(dto.getObservaciones());

        return self.guardarTransaccional(factura);
    }

    @Override
    @Transactional
    public void eliminarFactura(Long id) {
        Factura factura = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Factura no encontrada con ID: " + id));

        if (factura.getEstado() != EstadoFactura.PENDIENTE) {
            throw new EntityConflictException(
                    "Solo se pueden eliminar facturas en estado PENDIENTE.");
        }

        repository.deleteById(id);
        log.info("[ms-billing] Factura eliminada id={}", id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FacturaResponseDTO guardarTransaccional(Factura factura) {
        return mapToResponseDTO(repository.save(factura));
    }

    @Override
    public List<FacturaResponseDTO> listarTodas() {
        return repository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public FacturaResponseDTO obtenerPorId(Long id) {
        return mapToResponseDTO(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Factura no encontrada con ID: " + id)));
    }

    @Override
    public List<FacturaResponseDTO> listarPorEstado(EstadoFactura estado) {
        return repository.findByEstado(estado)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FacturaResponseDTO> listarPorWorkOrder(Long workOrderId) {
        return repository.findByWorkOrderId(workOrderId)
                .map(f -> List.of(mapToResponseDTO(f)))
                .orElse(List.of());
    }

    @Override
    public List<FacturaResponseDTO> listarPorTecnico(Long tecnicoId) {
        return repository.findByTecnicoId(tecnicoId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FacturaResponseDTO> listarPorBuilding(Long buildingId) {
        return repository.findByBuildingId(buildingId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    private FacturaResponseDTO mapToResponseDTO(Factura f) {
        return new FacturaResponseDTO(
                f.getId(), f.getWorkOrderId(), f.getTecnicoId(), f.getBuildingId(),
                f.getDescripcion(), f.getMontoTotal(), f.getEstado(),
                f.getFechaEmision(), f.getFechaPago(), f.getObservaciones());
    }
}