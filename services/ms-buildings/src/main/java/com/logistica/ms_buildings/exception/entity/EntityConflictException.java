package com.logistica.ms_buildings.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: CONFLICTO (409)
 * Se lanza cuando se detecta un registro duplicado, por ejemplo un RUT de administrador
 * que ya está registrado en otro edificio del sistema.
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler.
 */
public class EntityConflictException extends RuntimeException {

    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}
