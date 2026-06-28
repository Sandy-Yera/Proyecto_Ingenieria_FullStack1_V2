package com.logistica.ms_payments.exception.entity;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }
}