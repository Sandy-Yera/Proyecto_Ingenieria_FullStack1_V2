package com.logistica.ms_notifications.exception.entity;

public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String mensaje) {
        super(mensaje);
    }
}