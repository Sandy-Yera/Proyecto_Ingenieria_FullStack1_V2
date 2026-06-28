package com.logistica.ms_payments.exception.entity;

public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String mensaje) {
        super(mensaje);
    }
}