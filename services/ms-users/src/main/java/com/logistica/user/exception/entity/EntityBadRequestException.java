package com.logistica.user.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: SOLICITUD INCORRECTA (400)
 * CORRECCIÓN: Se elimina la anotación errónea @ResponseStatus(HttpStatus.CONFLICT) 
 * que mezclaba un BadRequest con un código 409. Ahora el flujo queda unificado 
 * semánticamente y controlado por el GlobalExceptionHandler con un HTTP 400.
 */
public class EntityBadRequestException extends RuntimeException {
    
    public EntityBadRequestException(String mensaje) {
        super(mensaje);
    }
}