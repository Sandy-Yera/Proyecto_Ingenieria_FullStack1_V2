package com.logistica.ms_quotes.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: CONFLICTO (409)
 * Se lanza cuando se detecta un conflicto de datos, por ejemplo una transición
 * de estado inválida en una Cotizacion (ej: intentar aceptar una cotización rechazada).
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler de ms-quotes.
 */
public class EntityConflictException extends RuntimeException {

    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}
