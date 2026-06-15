package com.logistica.ms_staff.exception.entity;
/**
 * EXCEPCIÓN DE NEGOCIO: CONFLICTO (409)
 * OPTIMIZACIÓN: Se remueve la anotación @ResponseStatus para unificar la captura
 * centralizada en el GlobalExceptionHandler de ms-staff. Código limpio y desacoplado.
 */
public class EntityConflictException extends RuntimeException {

    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}