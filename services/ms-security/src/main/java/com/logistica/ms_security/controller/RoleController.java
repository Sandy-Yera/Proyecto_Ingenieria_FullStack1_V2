package com.logistica.ms_security.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_security.dto.RoleRequestDTO;  // 🟢 Nuevo: Import Request DTO
import com.logistica.ms_security.dto.RoleResponseDTO; // 🟢 Nuevo: Import Response DTO
import com.logistica.ms_security.service.IRoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    // CREAR
    @PostMapping
    public ResponseEntity<RoleResponseDTO> crearRole(@Valid @RequestBody RoleRequestDTO roleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.crearRole(roleDTO));
    }
    
    // LEER
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> listarRole() {
        List<RoleResponseDTO> listado = roleService.listarRole();
        
        return listado.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(listado);
    }
    
    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> actualizarRole(
            @Valid @RequestBody RoleRequestDTO datosActualizados,
            @PathVariable Long id) { 
        return ResponseEntity.ok(roleService.actualizarRole(id, datosActualizados));
    }
        
    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRole(@PathVariable Long id) {
        roleService.eliminarRole(id);
        return ResponseEntity.noContent().build();
    }
}