package com.logistica.user.exception;

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

import com.logistica.user.exception.entity.EntityBadRequestException;
import com.logistica.user.exception.entity.EntityConflictException;
import com.logistica.user.exception.entity.EntityNotFoundException;

import feign.FeignException;

/**
 * CONTROLADOR GLOBAL DE EXCEPCIONES (ms-users)
 * OPTIMIZACIÓN: Se cambia @ControllerAdvice por @RestControllerAdvice para mejorar
 * el soporte de respuestas REST automatizadas y se estandariza el payload JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Helper utilitario para mantener la misma estructura exacta de JSON 
     * en todo el clúster de microservicios (Simetría con ms-auth).
     */
    private Map<String, Object> crearBaseBody(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        return body;
    }

    // ---- CORREGIDO: BAD REQUEST (400) ----
    @ExceptionHandler(EntityBadRequestException.class)
    public ResponseEntity<Object> handleUserBadRequest(EntityBadRequestException ex) {
        // CORRECCIÓN: Se repara el bug donde se inyectaba el código de estatus 404 dentro de una respuesta 400
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ---- NOT FOUND (404) ----
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(EntityNotFoundException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // ---- CONFLICT (409) ----
    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Object> handleUserConflict(EntityConflictException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // ---- LIBRERÍA: VALIDACIÓN DE JSON MALFORMADO (400) ----
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST, "Formato de JSON inválido o tipo de dato incorrecto en ms-users");
        body.put("details", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ---- LIBRERÍA: ERRORES DE ANOTACIONES EN DTOs / ENTIDADES (@Valid) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST, "Campos inválidos en la validación del perfil del usuario");
        
        // Mapeo funcional de los mensajes definidos en las anotaciones (@Email, @NotBlank, etc.)
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Campo no válido",
                        (existente, nuevo) -> existente
                ));

        body.put("errors", errores);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * ---- BLINDAJE CRÍTICO PARA MICROSERVICIOS (FeignException) ----
     * Captura de forma explícita cualquier error de comunicación con ms-auth.
     * Evita que el sistema lance un error 500 genérico y propaga el estatus original del fallo remoto.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignStatusException(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> body = crearBaseBody(status, "Error de sincronización con el servicio remoto de autenticación (ms-auth)");
        body.put("remoteDetails", ex.contentUTF8()); // Extrae el JSON de error que envió ms-auth
        
        return new ResponseEntity<>(body, status);
    }

    // ---- COMODÍN: ERRORES INTERNOS INESPERADOS (500) ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor de gestión de usuarios");
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}