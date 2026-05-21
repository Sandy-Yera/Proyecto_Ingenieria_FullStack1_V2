package com.logistica.ms_buildings.controller;

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

import com.logistica.ms_buildings.dto.EdificioRequestDTO;  // 🟢 Nuevo: Import Request DTO
import com.logistica.ms_buildings.dto.EdificioResponseDTO; // 🟢 Nuevo: Import Response DTO
import com.logistica.ms_buildings.service.EdificioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/edificios")
@RequiredArgsConstructor
public class EdificioController {

    private final EdificioService edificioService;
    
    // CREAR
    @PostMapping
    public ResponseEntity<EdificioResponseDTO> crearEdificio(@Valid @RequestBody EdificioRequestDTO edificioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(edificioService.crearEdificio(edificioDTO));
    }
    
    // LEER
    @GetMapping
    public ResponseEntity<List<EdificioResponseDTO>> listarEdificios() {
        List<EdificioResponseDTO> listado = edificioService.listarEdificios();
        
        return listado.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(listado);
    }
    
    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<EdificioResponseDTO> actualizarEdificio(
            @Valid @RequestBody EdificioRequestDTO datosActualizados,
            @PathVariable Long id) { 
        return ResponseEntity.ok(edificioService.actualizarEdificio(id, datosActualizados));
    }
        
    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEdificio(@PathVariable Long id) {
        edificioService.eliminarEdificio(id);
        return ResponseEntity.noContent().build();
    }
}