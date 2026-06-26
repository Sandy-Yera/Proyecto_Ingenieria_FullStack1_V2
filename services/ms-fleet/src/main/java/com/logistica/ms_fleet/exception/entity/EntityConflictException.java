package com.logistica.ms_fleet.exception.entity;

public class EntityConflictException extends RuntimeException {
    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}
