package com.logistica.ms_logistics.exception;

import com.logistica.ms_logistics.exception.entity.EntityBadRequestException;
import com.logistica.ms_logistics.exception.entity.EntityConflictException;
import com.logistica.ms_logistics.exception.entity.EntityCreationException;
import com.logistica.ms_logistics.exception.entity.EntityNotFoundException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> crearBaseBody(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        return body;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(crearBaseBody(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<Object> handleConflict(EntityConflictException ex) {
        return new ResponseEntity<>(crearBaseBody(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityBadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(EntityBadRequestException ex) {
        return new ResponseEntity<>(crearBaseBody(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityCreationException.class)
    public ResponseEntity<Object> handleCreationError(EntityCreationException ex) {
        return new ResponseEntity<>(crearBaseBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST, "Formato de JSON inválido o tipo de dato incorrecto");
        body.put("details", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return new ResponseEntity<>(crearBaseBody(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type no soportado. Asegúrate de enviar 'Content-Type: application/json'"),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParam(MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(crearBaseBody(HttpStatus.BAD_REQUEST,
                "Parámetro requerido ausente: '" + ex.getParameterName() + "'"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.BAD_REQUEST, "Errores de validación en los campos del formulario");
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Campo inválido",
                        (existente, nuevo) -> existente));
        body.put("errors", errores);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> body = crearBaseBody(status, "Error en comunicación con microservicio remoto");
        body.put("remoteDetails", ex.contentUTF8());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobal(Exception ex) {
        Map<String, Object> body = crearBaseBody(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor");
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
