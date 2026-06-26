package com.logistica.ms_schedule.exception.entity;

public class EntityConflictException extends RuntimeException {
    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}
