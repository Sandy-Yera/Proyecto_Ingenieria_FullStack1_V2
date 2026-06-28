package com.logistica.ms_billing.exception.entity;

public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String mensaje) {
        super(mensaje);
    }
}