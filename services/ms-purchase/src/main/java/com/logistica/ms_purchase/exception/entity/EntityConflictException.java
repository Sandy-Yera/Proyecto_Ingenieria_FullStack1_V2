package com.logistica.ms_purchase.exception.entity;

public class EntityConflictException extends RuntimeException {
    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}
