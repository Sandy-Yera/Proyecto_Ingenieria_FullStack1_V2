package com.logistica.ms_buildings.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: RECURSO NO ENCONTRADO (404)
 * Se lanza cuando se intenta acceder a un Edificio que no existe en la base de datos.
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }
}
