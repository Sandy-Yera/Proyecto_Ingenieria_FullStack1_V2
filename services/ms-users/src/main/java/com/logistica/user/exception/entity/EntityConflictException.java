package com.logistica.user.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: CONFLICTO (409)
 * OPTIMIZACIÓN: Se remueve la anotación @ResponseStatus ya que el control total 
 * del ciclo de vida del error y el formateo del JSON lo realiza el GlobalExceptionHandler.
 */
public class EntityConflictException extends RuntimeException {
    
    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}