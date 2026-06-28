package com.logistica.user.controller;

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

import com.logistica.user.dto.UserRegisterDTO;
import com.logistica.user.dto.UserResponseDTO;
import com.logistica.user.exception.entity.*;
import com.logistica.user.service.KafkaLogProducer;
import com.logistica.user.service.IUserService;

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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema BRM")
public class UserController {

    private final IUserService userService;
    private final KafkaLogProducer logProducer;

    // -- Listar Usuarios
    @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin usuarios", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listaUsers() {
        List<UserResponseDTO> listado = userService.listar();

        if (listado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.ok(listado);
        }
    }

    // -- CORREGIDO: Añadido @GetMapping("/existe") y cambiado el tipo de RUT a String
    @Operation(summary = "Verificar existencia de usuario", description = "Valida si existe un usuario por ID o por RUT (mutuamente excluyentes).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado de la verificación"),
        @ApiResponse(responseCode = "400", description = "Parámetros ambiguos o ausentes", content = @Content)
    })
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeUser(
            @Parameter(description = "ID del usuario") @RequestParam(required = false) Long id,
            @Parameter(description = "RUT del usuario") @RequestParam(required = false) String rut) {
        
        if (id != null && rut != null) {
            logProducer.sendLog("WARN",
                    "Petición a /existe rechazada: Parámetros ambiguos enviados simultáneamente (id=" + id
                            + ", rut=" + rut + ").");
            throw new EntityBadRequestException(
                    "Debe proporcionar solo un parámetro de búsqueda a la vez ('id' o 'rut').");
        }
        
        if (id != null) {
            return ResponseEntity.ok(userService.existeUserId(id));
        }
        
        if (rut != null) {
            return ResponseEntity.ok(userService.existeUserRut(rut));
        }
        
        logProducer.sendLog("WARN",
                "Petición a /existe rechazada: No se enviaron parámetros de búsqueda.");
        throw new EntityBadRequestException(
                "Se requiere al menos un parámetro de búsqueda válido ('id' o 'rut') para verificar la existencia del usuario");
    }

    // -- Crear Usuario
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario y genera sus credenciales remotas en ms-auth.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "RUT o correo ya registrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> crearUser(
            @Valid @RequestBody UserRegisterDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.crearUser(user));
    }

    // -- CORREGIDO: Sincronizado de @RequestParam a @PathVariable para cumplir con la ruta /{id}
    @Operation(summary = "Actualizar usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "RUT o correo ya registrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> actualizarUser(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Valid @RequestBody UserRegisterDTO datosActualizados) {
        return ResponseEntity.ok(userService.actualizarUser(id, datosActualizados));
    }

    // -- CORREGIDO: Sincronizado de @RequestParam a @PathVariable para cumplir con la ruta /{id}
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUserId(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        userService.eliminarUserId(id);
        return ResponseEntity.noContent().build(); // Nota: Cambiado a noContent() (24) por buena práctica REST en Delete
    }

    // -- Obtener usuario por ID
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> obtenerUserPorId(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        return ResponseEntity.ok(userService.encontrarUserId(id));
    }

    // -- Muestra el total de usuarios
    @Operation(summary = "Total de usuarios", description = "Retorna un mensaje con el conteo total de usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Mensaje retornado")
    @GetMapping("/total-usuarios")
    public ResponseEntity<String> mensajeTotalUsuarios() {
        String listado = userService.mensajeTotalUsuarios();
        return ResponseEntity.ok(listado);
    }
}