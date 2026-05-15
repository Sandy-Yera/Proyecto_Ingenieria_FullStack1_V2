package com.logistica.ms_auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_auth.exception.userCredencial.UserCredencialConflictException;
import com.logistica.ms_auth.exception.userCredencial.UserCredencialNotFoundException;
import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.service.UserCredencialService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserCredencialController {
    @Autowired
    private UserCredencialService userCredencialService;

    
    @GetMapping()
    public ResponseEntity<List<UserCredencial>> listar() {
        List<UserCredencial> listado = userCredencialService.listar();

        if (listado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.ok(listado);
        }
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> existeUser(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userCredencialService.existeUserCredencialId(id));
    }

    @PostMapping()
    public ResponseEntity<UserCredencial> crearUser(@Valid @RequestBody UserCredencial userCredencial) {
        if (!userCredencialService.existeUserCredencialId(userCredencial.getId())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userCredencialService.guardarUserCredencial(userCredencial));
        } else {
            throw new UserCredencialConflictException("Ya existe un usuario con el rut: " + userCredencial.getId());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserCredencial> actualizarUser(@Valid @RequestBody UserCredencial userCredencial, @PathVariable Long id) {
        if (!userCredencialService.existeUserCredencialId(id)) {
            throw new UserCredencialNotFoundException("No se encontró al usuario con la id: " + id);
        } else {
            userCredencial.setId(id);
            return ResponseEntity.ok(userCredencialService.guardarUserCredencial(userCredencial));
        }
    }
}
