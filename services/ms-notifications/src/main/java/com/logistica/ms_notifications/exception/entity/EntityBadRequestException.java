package com.logistica.ms_notifications.exception.entity;

public class EntityBadRequestException extends RuntimeException {
    public EntityBadRequestException(String mensaje) {
        super(mensaje);
    }
}