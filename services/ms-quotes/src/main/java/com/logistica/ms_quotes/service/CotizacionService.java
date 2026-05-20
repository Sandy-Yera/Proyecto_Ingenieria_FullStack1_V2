package com.logistica.ms_quotes.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.logistica.ms_quotes.client.BuildingClient; // Asegúrate de tener creadas estas interfaces Feign
import com.logistica.ms_quotes.client.UserClient;     // en tu paquete de clientes (com.logistica.ms_quotes.client)
import com.logistica.ms_quotes.exception.entity.EntityBadRequestException;
import com.logistica.ms_quotes.exception.entity.EntityConflictException;
import com.logistica.ms_quotes.exception.entity.EntityNotFoundException;
import com.logistica.ms_quotes.model.Cotizacion;
import com.logistica.ms_quotes.repository.CotizacionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    
    // Inyección de clientes Feign para resolver el Problema Crítico #1
    private final UserClient userClient;
    private final BuildingClient buildingClient;

    // CREAR
    @Transactional // Agregado para asegurar la consistencia transaccional de las llamadas distribuidas
    public Cotizacion crearCotizacion(Cotizacion cotizacion) {
        if (cotizacion.getId() != null && cotizacionRepository.existsById(cotizacion.getId())) {
            throw new EntityConflictException("Ya existe una cotización con este ID");
        }

        // 1. Validación remota del Usuario en ms-users via OpenFeign
        try {
            userClient.obtenerUsuarioPorId(cotizacion.getUserId());
        } catch (Exception e) {
            throw new EntityNotFoundException("No se puede crear la cotización. El Usuario con ID " 
                    + cotizacion.getUserId() + " no existe en el sistema.");
        }

        // 2. Validación remota del Edificio en ms-buildings via OpenFeign
        try {
            buildingClient.obtenerEdificioPorId(cotizacion.getBuildingId());
        } catch (Exception e) {
            throw new EntityNotFoundException("No se puede crear la cotización. El Edificio con ID " 
                    + cotizacion.getBuildingId() + " no existe en el sistema.");
        }

        return cotizacionRepository.save(cotizacion);
    }

    // LEER
    public List<Cotizacion> listarCotizaciones() {
        return cotizacionRepository.findAll();
    }

    // ACTUALIZAR
    @Transactional
    public Cotizacion actualizarCotizacion(Long id, Cotizacion cotizacion) {
        Cotizacion cotizacionExistente = cotizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. La cotización con ID " + id + " no existe."));

        if (cotizacion.getId() != null && !cotizacion.getId().equals(id)) {
            throw new EntityBadRequestException("El id ingresado y el de la cotización no coinciden");
        }

        // Actualizar datos
        cotizacionExistente.setUserId(cotizacion.getUserId());
        cotizacionExistente.setBuildingId(cotizacion.getBuildingId());
        cotizacionExistente.setDescription(cotizacion.getDescription());
        cotizacionExistente.setCategory(cotizacion.getCategory());
        cotizacionExistente.setEstimatedAmount(cotizacion.getEstimatedAmount());
        cotizacionExistente.setStatus(cotizacion.getStatus());

        return cotizacionExistente;
    }

    // ELIMINAR
    @Transactional
    public void eliminarCotizacion(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la cotización a eliminar.");
        }
        cotizacionRepository.deleteById(id);
    }
}