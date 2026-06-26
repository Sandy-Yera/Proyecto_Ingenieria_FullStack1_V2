package com.logistica.ms_auth.service;

import java.util.List;

import com.logistica.ms_auth.dto.ActualizarUsernameDTO;
import com.logistica.ms_auth.dto.UserCredencialRegisterDTO;
import com.logistica.ms_auth.dto.UserCredencialResponseDTO;

public interface IUserCredencialService {
    List<UserCredencialResponseDTO> listar();

    UserCredencialResponseDTO crearUserCredencial(UserCredencialRegisterDTO dto);

    Boolean existeUserCredencialId(Long id);

    Boolean existeUserCredencialUsername(String username);

    UserCredencialResponseDTO encontrarUserCredencialId(Long id);

    UserCredencialResponseDTO actualizarUserCredencial(Long id, UserCredencialRegisterDTO dto);

    UserCredencialResponseDTO actualizarPorUserId(Long userId, ActualizarUsernameDTO dto);

    void eliminarUserCredencial(Long id);
}
