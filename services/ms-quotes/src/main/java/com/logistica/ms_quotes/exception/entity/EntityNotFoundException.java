package com.logistica.ms_quotes.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: RECURSO NO ENCONTRADO (404)
 * Se lanza cuando se intenta acceder a una Cotizacion que no existe en la base de datos.
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler de ms-quotes.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }
}
