package com.logistica.ms_quotes.service;

import java.util.List;
import org.springframework.context.annotation.Lazy; // 🟢 Import para auto-inyección perezosa
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_quotes.client.BuildingClient; 
import com.logistica.ms_quotes.client.UserClient;     
import com.logistica.ms_quotes.exception.entity.EntityBadRequestException;
import com.logistica.ms_quotes.exception.entity.EntityConflictException;
import com.logistica.ms_quotes.exception.entity.EntityNotFoundException;
import com.logistica.ms_quotes.model.Cotizacion;
import com.logistica.ms_quotes.repository.CotizacionRepository;

@Service
@Transactional(readOnly = true) // 🟢 Configura lectura optimizada por defecto (listarCotizaciones() se beneficia de esto)
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final UserClient userClient;
    private final BuildingClient buildingClient;
    
    // 🟢 Inyección perezosa de sí mismo para obligar a Spring a interceptar los métodos transaccionales internos
    private final CotizacionService self;

    // Constructor unificado con Lombok reemplazado manualmente para poder inyectar de forma segura @Lazy
    public CotizacionService(CotizacionRepository cotizacionRepository, 
                             UserClient userClient, 
                             BuildingClient buildingClient, 
                             @Lazy CotizacionService self) {
        this.cotizacionRepository = cotizacionRepository;
        this.userClient = userClient;
        this.buildingClient = buildingClient;
        this.self = self;
    }

    // CREAR (Paso 1: Fuera de la Transacción)
    // 🔴 NOTA: Se removió @Transactional de aquí. Las llamadas Feign NO consumen conexiones de base de datos.
    public Cotizacion crearCotizacion(Cotizacion cotizacion) {
        // 1. Validación remota del Usuario en ms-users via OpenFeign (Fuera de Tx)
        try {
            userClient.obtenerUsuarioPorId(cotizacion.getUserId());
        } catch (Exception e) {
            throw new EntityNotFoundException("No se puede crear la cotización. El Usuario con ID " 
                    + cotizacion.getUserId() + " no existe en el sistema.");
        }

        // 2. Validación remota del Edificio en ms-buildings via OpenFeign (Fuera de Tx)
        try {
            buildingClient.obtenerEdificioPorId(cotizacion.getBuildingId());
        } catch (Exception e) {
            throw new EntityNotFoundException("No se puede crear la cotización. El Edificio con ID " 
                    + cotizacion.getBuildingId() + " no existe en el sistema.");
        }

        // 3. Si las redes respondieron bien, delegamos la persistencia atómica pasando a través del proxy
        return self.guardarCotizacionTransaccional(cotizacion);
    }

    // CREAR (Paso 2: Persistencia Aislada y Transaccional)
    @Transactional // 🟢 Abre la transacción en el último momento posible, garantizando atomicidad
    public Cotizacion guardarCotizacionTransaccional(Cotizacion cotizacion) {
        if (cotizacion.getId() != null && cotizacionRepository.existsById(cotizacion.getId())) {
            throw new EntityConflictException("Ya existe una cotización con este ID");
        }
        return cotizacionRepository.save(cotizacion);
    }

    // LEER
    public List<Cotizacion> listarCotizaciones() {
        return cotizacionRepository.findAll();
    }

    // ACTUALIZAR
    @Transactional // 🟢 Requiere escritura y Dirty Checking
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
    @Transactional // 🟢 Requiere transacción para eliminación segura
    public void eliminarCotizacion(Long id) {
        if (!cotizacionRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró la cotización a eliminar.");
        }
        cotizacionRepository.deleteById(id);
    }
}