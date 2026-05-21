package com.logistica.ms_auth.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_auth.dto.ActualizarUsernameDTO; // 🟢 NUEVO: Importación del DTO específico
import com.logistica.ms_auth.dto.UserCredencialRegisterDTO;
import com.logistica.ms_auth.dto.UserCredencialResponseDTO;
import com.logistica.ms_auth.exception.entity.*;
import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.repository.UserCredencialRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCredencialService {

    private final UserCredencialRepository userCredencialRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaLogProducer logProducer;
    private final HttpServletRequest request;

    @Transactional
    public UserCredencialResponseDTO crearUserCredencial(UserCredencialRegisterDTO dto) {
        String traceId = request.getHeader("X-Trace-Id");

        if (dto.getId() == null) {
            logProducer.sendLog("ERROR", "Intento de crear credencial sin ID de usuario. | TraceId: " + traceId);
            throw new EntityBadRequestException("El ID del usuario es obligatorio para registrar credenciales.");
        }

        if (userCredencialRepository.existsByUsername(dto.getUsername())) {
            logProducer.sendLog("WARN", "Conflicto de seguridad. El Username ya existe: " + dto.getUsername() + " | TraceId: " + traceId);
            throw new EntityConflictException("El email o username ya se encuentra registrado en el módulo de autenticación.");
        }

        UserCredencial userCredencial = new UserCredencial();
        userCredencial.setId(dto.getId());
        userCredencial.setUsername(dto.getUsername());
        userCredencial.setPassword(passwordEncoder.encode(dto.getPassword()));
        userCredencial.setIsActive(true);

        UserCredencial guardado = userCredencialRepository.save(userCredencial);

        logProducer.sendLog("INFO", "Credenciales asignadas correctamente al ID: " + guardado.getId() + " | TraceId: " + traceId);

        return convertirAResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<UserCredencialResponseDTO> listar() {
        return userCredencialRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public Boolean existeUserCredencialId(Long id) {
        return userCredencialRepository.existsById(id);
    }

    public Boolean existeUserCredencialUsername(String username) {
        return userCredencialRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public UserCredencialResponseDTO encontrarUserCredencialId(Long id) {
        String traceId = request.getHeader("X-Trace-Id");
        return convertirAResponseDTO(userCredencialRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Búsqueda fallida de credencial inexistente con ID: " + id + " | TraceId: " + traceId);
                    return new EntityNotFoundException("No se encontraron credenciales para el identificador proporcionado.");
                }));
    }

    @Transactional
    public UserCredencialResponseDTO actualizarUserCredencial(Long id, UserCredencialRegisterDTO dto) {
        String traceId = request.getHeader("X-Trace-Id");

        UserCredencial usuarioExistente = userCredencialRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Intento fallido de actualizar credencial inexistente con ID: " + id + " | TraceId: " + traceId);
                    return new EntityNotFoundException("No se puede actualizar. El usuario no existe.");
                });

        if (!usuarioExistente.getUsername().equals(dto.getUsername()) &&
                userCredencialRepository.existsByUsername(dto.getUsername())) {
            logProducer.sendLog("WARN", "Conflicto al actualizar ID " + id + ". El Username '" + dto.getUsername() + "' ya está ocupado. | TraceId: " + traceId);
            throw new EntityConflictException("El nuevo Username ya está en uso por otra entidad.");
        }

        usuarioExistente.setUsername(dto.getUsername());
        usuarioExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getIsActive() != null) {
            usuarioExistente.setIsActive(dto.getIsActive());
        }

        logProducer.sendLog("INFO", "Credenciales del ID " + id + " actualizadas con éxito. | TraceId: " + traceId);
        return convertirAResponseDTO(usuarioExistente);
    }

    /**
     * 🟢 METODO REFACTORIZADO (Etapa 3):
     * Ahora recibe de forma estricta y segura el ActualizarUsernameDTO.
     * Sincroniza el correo aislando por completo las contraseñas.
     */
    @Transactional
    public UserCredencialResponseDTO actualizarPorUserId(Long userId, ActualizarUsernameDTO dto) { // 🟢 Corrección: Tipo de DTO modificado
        String traceId = request.getHeader("X-Trace-Id");

        // Dado que el ID de la credencial mapea 1:1 con el ID del usuario, usamos findById
        UserCredencial credencialExistente = userCredencialRepository.findById(userId)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Intento fallido de sincronizar correo. No existen credenciales para el ID de Usuario: " + userId + " | TraceId: " + traceId);
                    return new EntityNotFoundException("No se encontraron credenciales de autenticación para el usuario con ID " + userId);
                });

        // Validar que el nuevo correo electrónico no lo tenga tomado otro usuario en ms-auth
        if (!credencialExistente.getUsername().equalsIgnoreCase(dto.getUsername()) &&
                userCredencialRepository.existsByUsername(dto.getUsername())) {
            logProducer.sendLog("WARN", "Conflicto de sincronización para Usuario ID " + userId + ". El Username '" + dto.getUsername() + "' ya existe en el sistema de seguridad. | TraceId: " + traceId);
            throw new EntityConflictException("El nuevo correo electrónico ya se encuentra registrado por otro usuario.");
        }

        // Sincronización atómica: Modificamos única y exclusivamente el username de manera segura
        credencialExistente.setUsername(dto.getUsername());

        logProducer.sendLog("INFO", "Sincronización de credencial exitosa. Username del Usuario ID " + userId + " cambiado a: " + dto.getUsername() + " | TraceId: " + traceId);
        
        return convertirAResponseDTO(credencialExistente);
    }

    @Transactional
    public void eliminarUserCredencial(Long id) {
        String traceId = request.getHeader("X-Trace-Id");
        if (!userCredencialRepository.existsById(id)) {
            logProducer.sendLog("WARN", "Intento fallido de eliminar credencial inexistente con ID: " + id + " | TraceId: " + traceId);
            throw new EntityNotFoundException("No se encontró el registro de autenticación a eliminar.");
        }

        userCredencialRepository.deleteById(id);
        logProducer.sendLog("INFO", "Credenciales del ID " + id + " eliminadas de la base de datos de seguridad. | TraceId: " + traceId);
    }

    public UserCredencialResponseDTO convertirAResponseDTO(UserCredencial userCredencial) {
        UserCredencialResponseDTO response = new UserCredencialResponseDTO();
        response.setId(userCredencial.getId());
        response.setUsername(userCredencial.getUsername());
        response.setIsActive(userCredencial.getIsActive());
        response.setLastLogin(userCredencial.getLastLogin());
        return response;
    }
}