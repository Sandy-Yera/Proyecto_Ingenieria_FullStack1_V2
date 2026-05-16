package com.logistica.ms_auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.repository.UserCredencialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCredencialService {
    private final UserCredencialRepository userCredencialRepository;

    /*
    CRUD
    - LISTAR
    - ACTUALIZAR
    - ELIMINAR
    */

    // LISTAR
    public List<UserCredencial> listar() {
        return userCredencialRepository.findAll();
    }

    // Encontrar un UserCredencial por id
    public Boolean existeUserCredencialId(Long id) {
        return userCredencialRepository.existsById(id);
    }

    // ACTUALIZAR y CREAR
    // Comentario Temporal
    // Utilizamos save() del JpaRepository para guardar y actualizar el model
    public UserCredencial guardarUserCredencial(UserCredencial userCredencial) {
        return userCredencialRepository.save(userCredencial);
    }

    // Eliminar
    // Comentario temporal
    // Ahora pedimos al "Eliminar" que retorne un boolean para saber facilmente si se ejecuto la acción o no
    public Boolean eliminarUserCredencial(UserCredencial userCredencial) {
        // Usamos un TryCatch para saber si el delete se ejecuto bien o no
        try {
            userCredencialRepository.delete(userCredencial);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
