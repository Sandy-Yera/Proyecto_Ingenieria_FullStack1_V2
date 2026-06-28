package com.logistica.ms_payments.service.impl;

import com.logistica.ms_payments.client.BillingClient;
import com.logistica.ms_payments.dto.CambioEstadoRequestDTO;
import com.logistica.ms_payments.dto.PagoRequestDTO;
import com.logistica.ms_payments.dto.PagoResponseDTO;
import com.logistica.ms_payments.dto.feign.BillingCambioEstadoRequestDTO;
import com.logistica.ms_payments.dto.feign.FacturaFeignDTO;
import com.logistica.ms_payments.exception.entity.EntityBadRequestException;
import com.logistica.ms_payments.exception.entity.EntityConflictException;
import com.logistica.ms_payments.exception.entity.EntityNotFoundException;
import com.logistica.ms_payments.model.EstadoPago;
import com.logistica.ms_payments.model.MetodoPago;
import com.logistica.ms_payments.model.Pago;
import com.logistica.ms_payments.repository.PagoRepository;
import com.logistica.ms_payments.service.PagoService;
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
public class PagoServiceImpl implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final PagoRepository repository;
    private final BillingClient billingClient;
    private final PagoService self;

    public PagoServiceImpl(PagoRepository repository,
            BillingClient billingClient,
            @Lazy PagoService self) {
        this.repository = repository;
        this.billingClient = billingClient;
        this.self = self;
    }

    @Override
    public PagoResponseDTO procesarPago(PagoRequestDTO dto) {
        log.info("[ms-payments] Procesando pago para facturaId={}", dto.getFacturaId());

        FacturaFeignDTO factura;
        try {
            factura = billingClient.obtenerFactura(dto.getFacturaId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "Factura con ID " + dto.getFacturaId() + " no existe.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-billing: " + e.getMessage());
        }

        if (!factura.getEstado().equals("PENDIENTE")) {
            throw new EntityConflictException(
                    "La factura ID " + dto.getFacturaId() + " no está PENDIENTE. Estado actual: "
                            + factura.getEstado());
        }

        if (!dto.getMonto().equals(factura.getMontoTotal())) {
            throw new EntityBadRequestException(
                    "El monto del pago (" + dto.getMonto()
                            + ") no coincide con el monto de la factura ("
                            + factura.getMontoTotal() + ")");
        }

        Pago pago = new Pago();
        pago.setFacturaId(dto.getFacturaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setReferencia(dto.getReferencia());

        PagoResponseDTO pagoGuardado = self.guardarTransaccional(pago);

        billingClient.pagarFactura(dto.getFacturaId(),
                new BillingCambioEstadoRequestDTO(
                        "Pago " + dto.getMetodoPago() + " - ref: " + dto.getReferencia()));

        Pago pagoActualizado = repository.findById(pagoGuardado.getId())
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado tras guardar."));
        pagoActualizado.setEstado(EstadoPago.COMPLETADO);
        pagoActualizado.setFechaActualizacion(LocalDateTime.now());

        return self.guardarTransaccional(pagoActualizado);
    }

    @Override
    public PagoResponseDTO marcarFallido(Long id, CambioEstadoRequestDTO dto) {
        log.info("[ms-payments] Marcando fallido pago id={}", id);

        Pago pago = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pago no encontrado con ID: " + id));

        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            throw new EntityConflictException(
                    "Solo se puede marcar como fallido un pago completado. Estado actual: "
                            + pago.getEstado());
        }

        pago.setEstado(EstadoPago.FALLIDO);
        pago.setObservaciones(dto.getObservaciones());
        pago.setFechaActualizacion(LocalDateTime.now());

        return self.guardarTransaccional(pago);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PagoResponseDTO guardarTransaccional(Pago pago) {
        return mapToResponseDTO(repository.save(pago));
    }

    @Override
    public List<PagoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PagoResponseDTO obtenerPorId(Long id) {
        return mapToResponseDTO(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pago no encontrado con ID: " + id)));
    }

    @Override
    public List<PagoResponseDTO> listarPorFactura(Long facturaId) {
        return repository.findByFacturaId(facturaId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<PagoResponseDTO> listarPorEstado(EstadoPago estado) {
        return repository.findByEstado(estado)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<PagoResponseDTO> listarPorMetodo(MetodoPago metodo) {
        return repository.findByMetodoPago(metodo)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    private PagoResponseDTO mapToResponseDTO(Pago p) {
        return new PagoResponseDTO(
                p.getId(), p.getFacturaId(), p.getMonto(), p.getMetodoPago(),
                p.getEstado(), p.getReferencia(), p.getFechaPago(),
                p.getFechaActualizacion(), p.getObservaciones());
    }
}