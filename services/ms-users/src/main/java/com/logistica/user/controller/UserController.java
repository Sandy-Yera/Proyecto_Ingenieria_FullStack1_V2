package com.logistica.user.controller;

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

import com.logistica.user.model.User;
import com.logistica.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/existe/{rut}")
    public ResponseEntity<Boolean> existeUser(@PathVariable Integer rut) {
        if (rut == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.existeUserRut(rut));
    }

    @PostMapping()
    public ResponseEntity<User> crearUser(@Valid @RequestBody User user) {
        if (!userService.existeUserRut(user.getRut())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.guardarUser(user));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> actualizarUser(@Valid @RequestBody User user, @PathVariable Long id) {
        if (!userService.existeUserId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            user.setId(id);
            return ResponseEntity.ok(userService.guardarUser(user));
        }
    }
}
