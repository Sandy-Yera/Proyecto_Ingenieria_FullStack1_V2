package com.logistica.ms_quotes.exception.entity;

/**
 * EXCEPCIÓN DE NEGOCIO: SOLICITUD INCORRECTA (400)
 * Se lanza cuando los datos de entrada son semánticamente inválidos,
 * por ejemplo datos contradictorios o un monto negativo que pasa la validación inicial.
 * El manejo centralizado y el formateo de respuesta se delegan al GlobalExceptionHandler de ms-quotes.
 */
public class EntityBadRequestException extends RuntimeException {

    public EntityBadRequestException(String mensaje) {
        super(mensaje);
    }
}
