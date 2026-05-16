package com.logistica.ms_auth.exception.userCredencial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserCredencialConflictException extends RuntimeException {
    public UserCredencialConflictException(String mensaje) {
        super(mensaje);
    }
}