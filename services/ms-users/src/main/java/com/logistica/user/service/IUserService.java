package com.logistica.user.service;

import java.util.List;

import com.logistica.user.dto.UserRegisterDTO;
import com.logistica.user.dto.UserResponseDTO;

public interface IUserService {
    List<UserResponseDTO> listar();

    Boolean existeUserId(Long id);

    Boolean existeUserRut(String rut);

    UserResponseDTO encontrarUserId(Long id);

    UserResponseDTO encontrarUserRut(String rut);

    UserResponseDTO crearUser(UserRegisterDTO dto);

    UserResponseDTO actualizarUser(Long id, UserRegisterDTO UserRegisterDTO);

    void eliminarUserId(Long id);

    String mensajeTotalUsuarios();
}
