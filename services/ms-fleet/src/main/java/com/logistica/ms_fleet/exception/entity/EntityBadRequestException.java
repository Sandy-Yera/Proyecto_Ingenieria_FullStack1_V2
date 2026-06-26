package com.logistica.ms_fleet.exception.entity;

public class EntityBadRequestException extends RuntimeException {
    public EntityBadRequestException(String mensaje) {
        super(mensaje);
    }
}
