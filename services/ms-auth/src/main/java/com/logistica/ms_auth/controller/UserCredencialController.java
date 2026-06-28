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
import com.logistica.ms_auth.service.IUserCredencialService;

import com.logistica.ms_auth.service.AuthService;
import com.logistica.ms_auth.dto.LoginRequestDTO;
import com.logistica.ms_auth.dto.LoginResponseDTO;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Gestión de credenciales de usuario y autenticación JWT")
public class UserCredencialController {

    private final IUserCredencialService userCredencialService;
    private final KafkaLogProducer logProducer;
    private final AuthService authService;

    /**
     * --- LISTAR CREDENCIALES ---
     * OPTIMIZACIÓN: Se reemplazó la estructura condicional if-else por una
     * expresión ternaria funcional mucho más limpia y directa (Clean Code).
     */
    @Operation(summary = "Listar credenciales", description = "Retorna todas las credenciales de usuario registradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin credenciales", content = @Content)
    })
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
    @Operation(summary = "Verificar existencia de credencial", description = "Valida si existe una credencial por ID o por username.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado de la verificación"),
        @ApiResponse(responseCode = "400", description = "No se envió ni id ni username", content = @Content)
    })
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeUser(
            @Parameter(description = "ID de la credencial") @RequestParam(required = false) Long id,
            @Parameter(description = "Username de la credencial") @RequestParam(required = false) String username) {
        if (id != null) {
            return ResponseEntity.ok(userCredencialService.existeUserCredencialId(id));
        }
        if (username != null) {
            return ResponseEntity.ok(userCredencialService.existeUserCredencialUsername(username));
        }

        logProducer.sendLog("WARN", "Petición a /existe rechazada: No se enviaron parámetros de búsqueda.");
        throw new EntityBadRequestException(
                "Debes enviar un id o un username en los parámetros de la consulta (?id=... o ?username=...)");
    }

    /**
     * --- ENDPOINT RECEPTOR (CREAR) ---
     * Mapea directamente a POST /api/auth
     * Recibe la llamada remota de ms-users mediante OpenFeign para persistir
     * credenciales.
     */
    @Operation(summary = "Crear credencial", description = "Persiste una nueva credencial de usuario. Endpoint receptor de la llamada remota de ms-users.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Credencial creada",
            content = @Content(schema = @Schema(implementation = UserCredencialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Username duplicado", content = @Content)
    })
    @PostMapping()
    public ResponseEntity<UserCredencialResponseDTO> crearUser(
            @Valid @RequestBody UserCredencialRegisterDTO userCredencial) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userCredencialService.crearUserCredencial(userCredencial));
    }

    /**
     * --- ACTUALIZAR POR ID CREDENCIAL ---
     * Mapea a PUT /api/auth/{id} usando @PathVariable para capturar el
     * identificador.
     */
    @Operation(summary = "Actualizar credencial por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credencial actualizada",
            content = @Content(schema = @Schema(implementation = UserCredencialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Credencial no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserCredencialResponseDTO> actualizarUser(
            @Valid @RequestBody UserCredencialRegisterDTO datosActualizados,
            @Parameter(description = "ID de la credencial") @PathVariable Long id) {
        return ResponseEntity.ok(userCredencialService.actualizarUserCredencial(id, datosActualizados));
    }

    /**
     * 🟢 ENDPOINT REFACTORIZADO (Solución Etapa 3):
     * Mapea a PUT /api/auth/usuario/{userId}
     * Ahora recibe únicamente ActualizarUsernameDTO, eliminando el riesgo de
     * procesar o exigir passwords.
     */
    @Operation(summary = "Actualizar username por ID de usuario", description = "Actualiza únicamente el username asociado a un userId, sin exigir passwords.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Username actualizado",
            content = @Content(schema = @Schema(implementation = UserCredencialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Credencial no encontrada", content = @Content)
    })
    @PutMapping("/usuario/{userId}")
    public ResponseEntity<UserCredencialResponseDTO> actualizarUserPorUserId(
            @Valid @RequestBody ActualizarUsernameDTO datosActualizados, // 🟢 Corrección: Tipo de DTO modificado
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        return ResponseEntity.ok(userCredencialService.actualizarPorUserId(userId, datosActualizados));
    }

    /**
     * --- ELIMINAR ---
     * Mapea a DELETE /api/auth/{id} y retorna un estado 204 No Content en caso de
     * éxito.
     */
    @Operation(summary = "Eliminar credencial")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Credencial eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Credencial no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUserCredencial(
            @Parameter(description = "ID de la credencial") @PathVariable Long id) {
        userCredencialService.eliminarUserCredencial(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * --- Métodos de login JWT ----
     */
    // AuthController.java — añadir endpoint de login al UserCredencialController
    // existente
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y retorna un token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // Endpoint de validación para que el Gateway pueda verificar tokens
    @Operation(summary = "Validar token JWT", description = "Endpoint usado por el Gateway para verificar la validez de un token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token inválido o ausente", content = @Content)
    })
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @Parameter(description = "Header Authorization con formato 'Bearer <token>'")
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok().build(); // 200 OK sin cuerpo
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
    }
}