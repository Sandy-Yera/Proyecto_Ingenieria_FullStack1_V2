package com.logistica.ms_quotes.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.logistica.ms_quotes.exception.entity.EntityBadRequestException;
import com.logistica.ms_quotes.exception.entity.EntityConflictException;
import com.logistica.ms_quotes.exception.entity.EntityCreationException;
import com.logistica.ms_quotes.exception.entity.EntityNotFoundException;

import feign.FeignException;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES — ms-quotes
 * Captura de forma centralizada todas las excepciones del dominio de Cotizaciones.
 * Produce un JSON estandarizado con: timestamp, status, error, message.
 * Estructura simétrica con el resto del clúster BRM.
 *
 * CORRECCIÓN: Javadocs actualizados de "ms-auth" a "ms-quotes".
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> crearBaseBody(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   mensaje);
        return body;
    }

    // ---- NOT FOUND (404) ----
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // ---- CONFLICT (409) ----
    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Object> handleEntityConflict(EntityConflictException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // ---- BAD REQUEST (400) ----
    @ExceptionHandler(EntityBadRequestException.class)
    public ResponseEntity<Object> handleEntityBadRequest(EntityBadRequestException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ---- INTERNAL SERVER ERROR (500) — Fallo de creación ----
    @ExceptionHandler(EntityCreationException.class)
    public ResponseEntity<Object> handleEntityCreation(EntityCreationException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ---- VALIDACIÓN DE JSON MALFORMADO ----
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST,
                "Formato de JSON inválido o tipo de dato incorrecto");
        body.put("details", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ---- VALIDACIÓN DE CAMPOS DTO (@Valid / @NotBlank / @NotNull) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST,
                "Errores de validación en los campos del formulario");

        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Campo inválido",
                        (existente, nuevo) -> existente
                ));

        body.put("errors", errores);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * BLINDAJE PARA MICROSERVICIOS (FeignException)
     * Captura respuestas fallidas de llamadas HTTP remotas entre microservicios del clúster BRM.
     * Mantiene el código HTTP original del microservicio remoto en lugar de convertirlo en 500.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignStatusException(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> body = crearBaseBody(status,
                "Error en comunicación con microservicio remoto");
        body.put("remoteDetails", ex.contentUTF8());

        return new ResponseEntity<>(body, status);
    }

    // ---- ERROR GENÉRICO INESPERADO (500) ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor de cotizaciones");
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
