package com.logistica.ms_quotes.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: ERROR INTERNO DE CREACIÓN (500)
 * Se lanza cuando falla la persistencia de una Cotizacion por un error interno inesperado,
 * como una violación de constraint de base de datos no anticipada.
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler de ms-quotes.
 */
public class EntityCreationException extends RuntimeException {

    public EntityCreationException(String mensaje) {
        super(mensaje);
    }
}
