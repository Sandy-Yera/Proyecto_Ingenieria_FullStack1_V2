package com.logistica.ms_security.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_security.dto.RoleAssignmentRequestDTO;  // 🟢 Nuevo: Import Request DTO
import com.logistica.ms_security.dto.RoleAssignmentResponseDTO; // 🟢 Nuevo: Import Response DTO
import com.logistica.ms_security.service.RoleAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/role-assignments")
@RequiredArgsConstructor
public class RoleAssignmentController {

    private final RoleAssignmentService roleAssignmentService;

    // CREAR
    @PostMapping
    public ResponseEntity<RoleAssignmentResponseDTO> crearRoleAssignment(
            @Valid @RequestBody RoleAssignmentRequestDTO assignmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleAssignmentService.crearRoleAssignment(assignmentDTO));
    }

    // LEER
    @GetMapping
    public ResponseEntity<List<RoleAssignmentResponseDTO>> listarRoleAssignments() {
        List<RoleAssignmentResponseDTO> listado = roleAssignmentService.listarRoleAssignments();
        
        return listado.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(listado);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<RoleAssignmentResponseDTO> actualizarRoleAssignment(
            @Valid @RequestBody RoleAssignmentRequestDTO datosActualizados,
            @PathVariable Long id) { 
        return ResponseEntity.ok(roleAssignmentService.actualizarRoleAssignment(id, datosActualizados));
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRoleAssignment(@PathVariable @NonNull Long id) {
        roleAssignmentService.eliminarRoleAssignment(id);
        return ResponseEntity.noContent().build();
    }
}