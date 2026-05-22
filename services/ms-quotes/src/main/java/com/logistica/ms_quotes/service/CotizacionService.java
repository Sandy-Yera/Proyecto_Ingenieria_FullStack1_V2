package com.logistica.ms_quotes.service;

import java.util.List;
import java.util.stream.Collectors;
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
import com.logistica.ms_quotes.repository.CotizacionRepository;

@Service
@Transactional(readOnly = true) 
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final UserClient userClient;
    private final BuildingClient buildingClient;
    private final CotizacionService self;

    public CotizacionService(CotizacionRepository cotizacionRepository, 
                             UserClient userClient, 
                             BuildingClient buildingClient, 
                             @Lazy CotizacionService self) {
        this.cotizacionRepository = cotizacionRepository;
        this.userClient = userClient;
        this.buildingClient = buildingClient;
        this.self = self;
    }

    // CREAR (Paso 1: Fuera de la Transacción - Sin retener conexiones Hikari)
    public CotizacionResponseDTO crearCotizacion(CotizacionRequestDTO dto) {
        // 1. Validación remota del Usuario en ms-users via OpenFeign con Excepciones Tipificadas
        try {
            userClient.obtenerUsuarioPorId(dto.getUserId());
        } catch (feign.FeignException.NotFound e) { // 🟢 Específico: El usuario realmente no existe (HTTP 404)
            throw new EntityNotFoundException("No se puede crear la cotización. El Usuario con ID " 
                    + dto.getUserId() + " no existe en el sistema.");
        } catch (feign.FeignException e) { // 🟢 Genérico de Feign: Caídas de red, Timeouts, HTTP 500
            throw new EntityBadRequestException("Error de comunicación remota con ms-users: " + e.getMessage());
        }

        // 2. Validación remota del Edificio en ms-buildings via OpenFeign con Excepciones Tipificadas
        try {
            buildingClient.obtenerEdificioPorId(dto.getBuildingId()); // 🟢 Corrección del nombre del método corregido en el cliente
        } catch (feign.FeignException.NotFound e) { // 🟢 Específico: El edificio realmente no existe (HTTP 404)
            throw new EntityNotFoundException("No se puede crear la cotización. El Edificio con ID " 
                    + dto.getBuildingId() + " no existe en el sistema.");
        } catch (feign.FeignException e) { // 🟢 Genérico de Feign: Caídas de red, Timeouts, HTTP 500
            throw new EntityBadRequestException("Error de comunicación remota con ms-buildings: " + e.getMessage());
        }

        // Mapear de Request DTO a Entidad antes de entrar a la transacción
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setUserId(dto.getUserId());
        cotizacion.setBuildingId(dto.getBuildingId());
        cotizacion.setDescription(dto.getDescription());
        cotizacion.setCategory(dto.getCategory());
        cotizacion.setEstimatedAmount(dto.getEstimatedAmount());
        if (dto.getStatus() != null) {
            cotizacion.setStatus(dto.getStatus());
        }

        // 3. Pasamos al proxy para ejecutar la persistencia atómica rápida
        return self.guardarCotizacionTransaccional(cotizacion);
    }

    // CREAR (Paso 2: Persistencia Aislada y Transaccional)
    @Transactional
    public CotizacionResponseDTO guardarCotizacionTransaccional(Cotizacion cotizacion) {
        if (cotizacion.getId() != null && cotizacionRepository.existsById(cotizacion.getId())) {
            throw new EntityConflictException("Ya existe una cotización con este ID");
        }
        Cotizacion guardada = cotizacionRepository.save(cotizacion);
        return convertToResponseDTO(guardada);
    }

    // LEER
    public List<CotizacionResponseDTO> listarCotizaciones() {
        return cotizacionRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ACTUALIZAR
    @Transactional
    public CotizacionResponseDTO actualizarCotizacion(Long id, CotizacionRequestDTO dto) {
        Cotizacion cotizacionExistente = cotizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. La cotización con ID " + id + " no existe."));

        // Sincronización mediante Dirty Checking usando los datos del Request DTO
        cotizacionExistente.setUserId(dto.getUserId());
        cotizacionExistente.setBuildingId(dto.getBuildingId());
        cotizacionExistente.setDescription(dto.getDescription());
        cotizacionExistente.setCategory(dto.getCategory());
        cotizacionExistente.setEstimatedAmount(dto.getEstimatedAmount());
        if (dto.getStatus() != null) {
            cotizacionExistente.setStatus(dto.getStatus());
        }

        return convertToResponseDTO(cotizacionExistente);
    }

    // ELIMINAR
    @Transactional
    public void eliminarCotizacion(Long id) {
        if (!cotizacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la cotización a eliminar.");
        }
        cotizacionRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES DE CONVERSIÓN (Mappers manuales)
    private CotizacionResponseDTO convertToResponseDTO(Cotizacion cotizacion) {
        CotizacionResponseDTO dto = new CotizacionResponseDTO();
        dto.setId(cotizacion.getId());
        dto.setUserId(cotizacion.getUserId());
        dto.setBuildingId(cotizacion.getBuildingId());
        dto.setDescription(cotizacion.getDescription());
        dto.setCategory(cotizacion.getCategory());
        dto.setEstimatedAmount(cotizacion.getEstimatedAmount());
        dto.setStatus(cotizacion.getStatus());
        dto.setCreatedAt(cotizacion.getCreatedAt());
        return dto;
    }
}