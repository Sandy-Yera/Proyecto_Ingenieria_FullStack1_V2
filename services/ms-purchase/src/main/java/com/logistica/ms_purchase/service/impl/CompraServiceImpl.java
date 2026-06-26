package com.logistica.ms_purchase.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_purchase.client.InventoryClient;
import com.logistica.ms_purchase.dto.CompraRequestDTO;
import com.logistica.ms_purchase.dto.CompraResponseDTO;
import com.logistica.ms_purchase.exception.entity.EntityBadRequestException;
import com.logistica.ms_purchase.exception.entity.EntityNotFoundException;
import com.logistica.ms_purchase.model.Compra;
import com.logistica.ms_purchase.repository.CompraRepository;
import com.logistica.ms_purchase.service.CompraService;

/**
 * Implementación del servicio de Compras.
 *
 * Patrón de dos fases para evitar retener conexiones del pool Hikari durante
 * llamadas remotas:
 *   Fase 1 (registrarCompra): valida material en ms-inventory vía Feign.
 *   Fase 2 (guardarCompraTransaccional via self-proxy): persiste atómicamente.
 *   Post-save: llama a ms-inventory para reabastecer el stock.
 */
@Service
@Transactional(readOnly = true)
public class CompraServiceImpl implements CompraService {

    private static final Logger log = LoggerFactory.getLogger(CompraServiceImpl.class);

    private final CompraRepository compraRepository;
    private final InventoryClient inventoryClient;
    private final CompraService self;

    public CompraServiceImpl(CompraRepository compraRepository,
            InventoryClient inventoryClient,
            @Lazy CompraService self) {
        this.compraRepository = compraRepository;
        this.inventoryClient = inventoryClient;
        this.self = self;
    }

    @Override
    public CompraResponseDTO registrarCompra(CompraRequestDTO dto) {
        log.info("[ms-purchase] Iniciando registro de compra materialId={} proveedor={}",
                dto.getMaterialId(), dto.getProveedor());

        // 1. Validar que el material existe en ms-inventory
        try {
            inventoryClient.obtenerMaterialPorId(dto.getMaterialId());
        } catch (feign.FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "No se puede registrar la compra. El material con ID "
                            + dto.getMaterialId() + " no existe en el inventario.");
        } catch (feign.FeignException e) {
            throw new EntityBadRequestException(
                    "Error de comunicación con ms-inventory: " + e.getMessage());
        }

        // 2. Persistir la compra de forma atómica via self-proxy
        CompraResponseDTO respuesta = self.guardarCompraTransaccional(mapToEntity(dto));
        log.info("[ms-purchase] Compra persistida id={}", respuesta.getId());

        // 3. Reabastecer stock en ms-inventory (una compra a proveedor aumenta el stock)
        try {
            inventoryClient.reabastecerStock(dto.getMaterialId(), dto.getCantidad());
            log.info("[ms-purchase] Stock actualizado en ms-inventory materialId={} cantidad={}",
                    dto.getMaterialId(), dto.getCantidad());
        } catch (feign.FeignException e) {
            log.error("[ms-purchase] Compra id={} registrada pero fallo el reabastecimiento: {}",
                    respuesta.getId(), e.getMessage());
            throw new EntityBadRequestException(
                    "Compra registrada (id=" + respuesta.getId()
                            + ") pero ocurrió un error al actualizar el stock: " + e.getMessage());
        }

        return respuesta;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompraResponseDTO guardarCompraTransaccional(Compra compra) {
        Compra guardada = compraRepository.save(compra);
        return mapToResponseDTO(guardada);
    }

    @Override
    public List<CompraResponseDTO> listarCompras() {
        List<CompraResponseDTO> lista = compraRepository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        log.info("[ms-purchase] Consulta todas las compras. Total: {}", lista.size());
        return lista;
    }

    @Override
    public CompraResponseDTO obtenerCompraPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ms-purchase] Compra no encontrada id={}", id);
                    return new EntityNotFoundException("Compra no encontrada con ID: " + id);
                });
        return mapToResponseDTO(compra);
    }

    @Override
    public List<CompraResponseDTO> listarPorMaterial(Long materialId) {
        List<CompraResponseDTO> lista = compraRepository.findByMaterialId(materialId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        log.info("[ms-purchase] Consulta compras por materialId={}. Total: {}", materialId, lista.size());
        return lista;
    }

    @Override
    @Transactional
    public CompraResponseDTO actualizarCompra(Long id, CompraRequestDTO dto) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se puede actualizar. Compra con ID " + id + " no existe."));

        compra.setMaterialId(dto.getMaterialId());
        compra.setProveedor(dto.getProveedor());
        compra.setCantidad(dto.getCantidad());
        compra.setPrecioUnitario(dto.getPrecioUnitario());
        compra.setTotalCosto(dto.getCantidad() * dto.getPrecioUnitario());

        log.info("[ms-purchase] Compra actualizada id={}", id);
        return mapToResponseDTO(compra);
    }

    @Override
    @Transactional
    public void eliminarCompra(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "No se puede eliminar. Compra con ID " + id + " no existe.");
        }
        compraRepository.deleteById(id);
        log.info("[ms-purchase] Compra eliminada id={}", id);
    }

    private Compra mapToEntity(CompraRequestDTO dto) {
        Compra c = new Compra();
        c.setMaterialId(dto.getMaterialId());
        c.setProveedor(dto.getProveedor());
        c.setCantidad(dto.getCantidad());
        c.setPrecioUnitario(dto.getPrecioUnitario());
        return c;
    }

    private CompraResponseDTO mapToResponseDTO(Compra c) {
        return new CompraResponseDTO(
                c.getId(), c.getMaterialId(), c.getProveedor(),
                c.getCantidad(), c.getPrecioUnitario(), c.getTotalCosto(), c.getCreatedAt());
    }
}
