package com.logistica.ms_staff.controller;

import com.logistica.ms_staff.dto.StaffRequestDTO;
import com.logistica.ms_staff.dto.StaffResponseDTO;
import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    // POST /api/v1/staff
    @PostMapping
    public ResponseEntity<StaffResponseDTO> crearTecnico(@Valid @RequestBody StaffRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.crearTecnico(dto));
    }

    // GET /api/v1/staff/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> obtenerTecnicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.obtenerTecnicoPorId(id));
    }

    // GET /api/v1/staff
    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> obtenerTodosTecnicos() {
        return ResponseEntity.ok(staffService.obtenerTodosTecnicos());
    }

    // GET /api/v1/staff/especialidad/{especialidad}
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<StaffResponseDTO>> obtenerPorEspecialidad(
            @PathVariable Especialidad especialidad) {
        return ResponseEntity.ok(staffService.obtenerTecnicosPorEspecialidad(especialidad));
    }

    // GET /api/v1/staff/disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<StaffResponseDTO>> obtenerDisponibles() {
        return ResponseEntity.ok(staffService.obtenerTecnicosDisponibles());
    }

    // GET /api/v1/staff/disponibles/{especialidad}
    // Endpoint clave para ms-logistics: asignación inteligente de técnicos
    @GetMapping("/disponibles/{especialidad}")
    public ResponseEntity<List<StaffResponseDTO>> obtenerDisponiblesPorEspecialidad(
            @PathVariable Especialidad especialidad) {
        return ResponseEntity.ok(staffService.obtenerTecnicosDisponiblesPorEspecialidad(especialidad));
    }

    // PUT /api/v1/staff/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> actualizarTecnico(
            @PathVariable Long id,
            @Valid @RequestBody StaffRequestDTO dto) {
        return ResponseEntity.ok(staffService.actualizarTecnico(id, dto));
    }

    // PATCH /api/v1/staff/{id}/disponibilidad
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<StaffResponseDTO> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponibilidad) {
        return ResponseEntity.ok(staffService.actualizarDisponibilidad(id, disponibilidad));
    }

    // DELETE /api/v1/staff/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTecnico(@PathVariable Long id) {
        staffService.eliminarTecnico(id);
        return ResponseEntity.noContent().build();
    }
}