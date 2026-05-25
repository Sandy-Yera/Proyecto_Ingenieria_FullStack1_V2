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

import com.logistica.ms_auth.dto.ActualizarUsernameDTO; 
import com.logistica.ms_auth.dto.UserCredencialRegisterDTO;
import com.logistica.ms_auth.dto.UserCredencialResponseDTO;
import com.logistica.ms_auth.exception.entity.EntityBadRequestException;
import com.logistica.ms_auth.service.KafkaLogProducer;
import com.logistica.ms_auth.service.UserCredencialService;

import com.logistica.ms_auth.service.AuthService;
import com.logistica.ms_auth.dto.LoginRequestDTO;
import com.logistica.ms_auth.dto.LoginResponseDTO;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserCredencialController {
    
    private final UserCredencialService userCredencialService;
    private final KafkaLogProducer logProducer;
    private final AuthService authService;

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
     * --- ACTUALIZAR POR ID CREDENCIAL ---
     * Mapea a PUT /api/auth/{id} usando @PathVariable para capturar el identificador.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserCredencialResponseDTO> actualizarUser(
            @Valid @RequestBody UserCredencialRegisterDTO datosActualizados,
            @PathVariable Long id) { 
        return ResponseEntity.ok(userCredencialService.actualizarUserCredencial(id, datosActualizados));
    }

    /**
     * 🟢 ENDPOINT REFACTORIZADO (Solución Etapa 3):
     * Mapea a PUT /api/auth/usuario/{userId}
     * Ahora recibe únicamente ActualizarUsernameDTO, eliminando el riesgo de procesar o exigir passwords.
     */
    @PutMapping("/usuario/{userId}")
    public ResponseEntity<UserCredencialResponseDTO> actualizarUserPorUserId(
            @Valid @RequestBody ActualizarUsernameDTO datosActualizados, // 🟢 Corrección: Tipo de DTO modificado
            @PathVariable Long userId) { 
        return ResponseEntity.ok(userCredencialService.actualizarPorUserId(userId, datosActualizados));
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


    /**
     * --- Métodos de login JWT ----
     */
    // AuthController.java — añadir endpoint de login al UserCredencialController existente
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // Endpoint de validación para que el Gateway pueda verificar tokens
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok(authService.validateToken(token));
    }
}