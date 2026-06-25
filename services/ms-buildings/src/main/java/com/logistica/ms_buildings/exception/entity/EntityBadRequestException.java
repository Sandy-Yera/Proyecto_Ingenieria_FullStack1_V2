package com.logistica.ms_buildings.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: SOLICITUD INCORRECTA (400)
 * Se lanza cuando los datos de entrada son semánticamente inválidos o contradictorios,
 * por ejemplo coordenadas nulas o un ID de path que no coincide con el del cuerpo.
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler.
 */
public class EntityBadRequestException extends RuntimeException {

    public EntityBadRequestException(String mensaje) {
        super(mensaje);
    }
}
