package com.logistica.ms_auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logistica.ms_auth.exception.userCredencial.EntityConflictException;
import com.logistica.ms_auth.exception.userCredencial.EntityNotFoundException;
import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.repository.UserCredencialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCredencialService {
    private final UserCredencialRepository userCredencialRepository;

    /*
     * CRUD
     * - LISTAR
     * - ACTUALIZAR
     * - ELIMINAR
     */

    // --- LISTAR --- READ
    public List<UserCredencial> listar() {
        return userCredencialRepository.findAll();
    }

    // Existe un UserCredencial por id
    public Boolean existeUserCredencialId(Long id) {
        return userCredencialRepository.existsById(id);
    }

    // Existe un UserCredencial por Username
    public Boolean existeUserCredencialUsername(String username) {
        return userCredencialRepository.existsByUsername(username);
    }

    // Encontrar un User Por su ID
    public UserCredencial encontrarUserCredencialId(Long id) {
        return userCredencialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró al usuario con la id: " + id));
    }

    // --- ACTUALIZAR y CREAR ---
    // Comentario Temporal
    // Utilizamos save() del JpaRepository para guardar y actualizar el model
    public UserCredencial guardarUserCredencial(UserCredencial userCredencial) {
        if (!userCredencialRepository.existsByUsername(userCredencial.getUsername())) {
            return userCredencialRepository.save(userCredencial);
        }

        throw new EntityConflictException("Ya existe el Username: " + userCredencial.getUsername());
    }

    public UserCredencial actualizarUserCredencial(Long id, UserCredencial datosActualizados) {
        // Verificamos que el usuario a actualizar exista realmente
        UserCredencial usuarioExistente = encontrarUserCredencialId(id);

        // 2. Validación de Username duplicado por OTRO usuario:
        // Si cambió su username, verificamos que el nuevo no esté tomado por alguien
        // más
        if (!usuarioExistente.getUsername().equals(datosActualizados.getUsername()) &&
                userCredencialRepository.existsByUsername(datosActualizados.getUsername())) {
            throw new EntityConflictException(
                    "El Username '" + datosActualizados.getUsername() + "' ya está en uso por otro usuario.");
        }

        // 3. Seteamos los cambios seguros
        usuarioExistente.setUsername(datosActualizados.getUsername());
        // usuarioExistente.setPassword(datosActualizados.getPassword()); // Ejemplo si
        // agregas más campos

        return userCredencialRepository.save(usuarioExistente);
    }

    // --- Eliminar ---
    // Comentario temporal
    // Ahora pedimos al "Eliminar" que retorne un boolean para saber facilmente si
    // se ejecuto la acción o no
    public void eliminarUserCredencial(Long id) {
        if (!userCredencialRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró al usuario con la id: " + id);
        }

        userCredencialRepository.deleteById(id);
    }
}
