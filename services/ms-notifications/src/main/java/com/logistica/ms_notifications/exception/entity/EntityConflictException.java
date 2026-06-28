package com.logistica.ms_notifications.exception.entity;

public class EntityConflictException extends RuntimeException {
    public EntityConflictException(String mensaje) {
        super(mensaje);
    }
}