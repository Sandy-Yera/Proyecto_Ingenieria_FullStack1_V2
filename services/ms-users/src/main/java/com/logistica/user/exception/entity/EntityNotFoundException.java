package com.logistica.user.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: RECURSO NO ENCONTRADO (404)
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }
}