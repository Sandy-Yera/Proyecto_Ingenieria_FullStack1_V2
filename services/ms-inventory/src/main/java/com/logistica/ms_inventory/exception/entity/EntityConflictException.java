package com.logistica.ms_inventory.exception.entity;

public class EntityConflictException extends RuntimeException {
    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}
