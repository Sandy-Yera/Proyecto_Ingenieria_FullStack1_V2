package com.logistica.ms_workorders.exception.entity;

public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String mensaje) {
        super(mensaje);
    }
}