package com.logistica.ms_auth.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_auth.dto.UserCredencialRegisterDTO;
import com.logistica.ms_auth.dto.UserCredencialResponseDTO;
import com.logistica.ms_auth.exception.entity.EntityBadRequestException;
import com.logistica.ms_auth.service.KafkaLogProducer;
import com.logistica.ms_auth.service.UserCredencialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserCredencialController {
    
    private final UserCredencialService userCredencialService;
    private final KafkaLogProducer logProducer;

    /**
     * --- LISTAR CREDENCIALES ---
     * OPTIMIZACIÓN: Se reemplazó la estructura condicional if-else por una
     * expresión ternaria funcional mucho más limpia y directa (Clean Code).
     */
    @GetMapping()
    public ResponseEntity<List<UserCredencialResponseDTO>> listar() {
        List<UserCredencialResponseDTO> listado = userCredencialService.listar();
        
        return listado.isEmpty() 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.ok(listado);
    }

    /**
     * --- VERIFICAR EXISTENCIA ---
     * Valida de manera condicional si existe una credencial por ID o por Username.
     */
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username) {
        if (id != null) {
            return ResponseEntity.ok(userCredencialService.existeUserCredencialId(id));
        }
        if (username != null) {
            return ResponseEntity.ok(userCredencialService.existeUserCredencialUsername(username));
        }

        logProducer.sendLog("WARN", "Petición a /existe rechazada: No se enviaron parámetros de búsqueda.");
        throw new EntityBadRequestException("Debes enviar un id o un username en los parámetros de la consulta (?id=... o ?username=...)");
    }

    /**
     * --- ENDPOINT RECEPTOR (CREAR) ---
     * Mapea directamente a POST /api/auth
     * Recibe la llamada remota de ms-users mediante OpenFeign para persistir credenciales.
     */
    @PostMapping()
    public ResponseEntity<UserCredencialResponseDTO> crearUser(
            @Valid @RequestBody UserCredencialRegisterDTO userCredencial) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userCredencialService.crearUserCredencial(userCredencial));
    }

    /**
     * --- ACTUALIZAR ---
     * Mapea a PUT /api/auth/{id} usando @PathVariable para capturar el identificador.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserCredencialResponseDTO> actualizarUser(
            @Valid @RequestBody UserCredencialRegisterDTO datosActualizados,
            @PathVariable Long id) { 
        return ResponseEntity.ok(userCredencialService.actualizarUserCredencial(id, datosActualizados));
    }

    /**
     * --- ELIMINAR ---
     * Mapea a DELETE /api/auth/{id} y retorna un estado 204 No Content en caso de éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUserCredencial(@PathVariable Long id) {
        userCredencialService.eliminarUserCredencial(id);
        return ResponseEntity.noContent().build();
    }
}