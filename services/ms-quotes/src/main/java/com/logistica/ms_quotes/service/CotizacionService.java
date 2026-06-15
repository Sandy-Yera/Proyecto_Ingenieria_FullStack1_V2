package com.logistica.ms_quotes.service;

import java.util.List;

import com.logistica.ms_quotes.dto.CotizacionRequestDTO;
import com.logistica.ms_quotes.dto.CotizacionResponseDTO;
import com.logistica.ms_quotes.model.Cotizacion;
import com.logistica.ms_quotes.model.Status;

/**
 * CONTRATO DEL SERVICIO — CotizacionService
 * Define las operaciones de negocio disponibles para el dominio de Cotizaciones.
 * La implementación concreta reside en impl/CotizacionServiceImpl.java.
 */
public interface CotizacionService {

    /**
     * Crea una nueva cotización. Valida usuario y edificio remotamente vía Feign
     * (ms-users / ms-buildings) antes de persistir. Asigna estado PENDING si no se especifica.
     */
    CotizacionResponseDTO crearCotizacion(CotizacionRequestDTO dto);

    /**
     * Persiste la cotización dentro de una transacción aislada, separada de las
     * llamadas remotas vía Feign realizadas en crearCotizacion (evita retener
     * conexiones del pool mientras se espera la respuesta de otros microservicios).
     */
    CotizacionResponseDTO guardarCotizacionTransaccional(Cotizacion cotizacion);

    /**
     * Retorna el listado completo de cotizaciones registradas.
     */
    List<CotizacionResponseDTO> listarCotizaciones();

    /**
     * Busca y retorna una cotización por su ID.
     */
    CotizacionResponseDTO obtenerCotizacionPorId(Long id);

    /**
     * Retorna todas las cotizaciones de un usuario específico.
     */
    List<CotizacionResponseDTO> listarPorUsuario(Long userId);

    /**
     * Retorna todas las cotizaciones filtradas por estado.
     */
    List<CotizacionResponseDTO> listarPorEstado(Status status);

    /**
     * Actualiza los datos de una cotización existente.
     */
    CotizacionResponseDTO actualizarCotizacion(Long id, CotizacionRequestDTO dto);

    /**
     * Elimina una cotización por su ID.
     */
    void eliminarCotizacion(Long id);
}
