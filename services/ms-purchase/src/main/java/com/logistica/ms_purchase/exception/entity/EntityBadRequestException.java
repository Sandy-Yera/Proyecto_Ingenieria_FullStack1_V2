package com.logistica.ms_purchase.exception.entity;

public class EntityBadRequestException extends RuntimeException {
    public EntityBadRequestException(String mensaje) {
        super(mensaje);
    }
}
