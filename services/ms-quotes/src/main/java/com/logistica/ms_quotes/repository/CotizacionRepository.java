package com.logistica.ms_quotes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistica.ms_quotes.model.Cotizacion;
import com.logistica.ms_quotes.model.Status;

/**
 * REPOSITORIO JPA — CotizacionRepository
 * Hereda operaciones CRUD completas de JpaRepository.
 * Se agregan consultas derivadas útiles para el dominio de cotizaciones.
 *
 * CORRECCIÓN: Se agrega @Repository explícito para consistencia con el clúster.
 * CORRECCIÓN: Se agregan findByUserId, findByStatus y findByBuildingId
 *             que antes estaban ausentes y son necesarias para filtros del dominio.
 */
@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    /**
     * Lista todas las cotizaciones asociadas a un usuario.
     * Usado para que un cliente consulte el historial de sus propias cotizaciones.
     */
    List<Cotizacion> findByUserId(Long userId);

    /**
     * Lista todas las cotizaciones de un edificio específico.
     * Usado por ms-buildings para consultar el historial de reparaciones de un edificio.
     */
    List<Cotizacion> findByBuildingId(Long buildingId);

    /**
     * Filtra cotizaciones por estado (PENDING, SENT, ACCEPTED, REJECTED).
     * Usado para el panel administrativo de gestión de cotizaciones.
     */
    List<Cotizacion> findByStatus(Status status);

    /**
     * Filtra cotizaciones de un usuario por estado.
     * Combinación útil para el panel personal del cliente.
     */
    List<Cotizacion> findByUserIdAndStatus(Long userId, Status status);
}
