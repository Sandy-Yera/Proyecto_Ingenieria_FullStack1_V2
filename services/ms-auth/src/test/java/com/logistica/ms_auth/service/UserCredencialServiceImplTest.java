package com.logistica.ms_auth.service;

import com.logistica.ms_auth.dto.ActualizarUsernameDTO;
import com.logistica.ms_auth.dto.UserCredencialRegisterDTO;
import com.logistica.ms_auth.exception.entity.EntityConflictException;
import com.logistica.ms_auth.exception.entity.EntityNotFoundException;
import com.logistica.ms_auth.model.UserCredencial;
import com.logistica.ms_auth.repository.UserCredencialRepository;
import com.logistica.ms_auth.service.impl.UserCredencialServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserCredencialServiceImplTest {

    @Mock
    UserCredencialRepository userCredencialRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    KafkaLogProducer logProducer;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    UserCredencialServiceImpl service;

    private static UserCredencialRegisterDTO dtoValido() {
        UserCredencialRegisterDTO dto = new UserCredencialRegisterDTO();
        dto.setId(1L);
        dto.setUsername("usuario@correo.com");
        dto.setPassword("clave123");
        return dto;
    }

    @Test
    void crearUserCredencial_cuandoUsernameYaExiste_lanzaEntityConflictException() {
        lenient().when(request.getHeader("X-Trace-Id")).thenReturn("trace-1");
        given(userCredencialRepository.existsByUsername("usuario@correo.com")).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearUserCredencial(dtoValido()));
        then(userCredencialRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearUserCredencial_cuandoDatosValidos_guardaLaCredencial() {
        lenient().when(request.getHeader("X-Trace-Id")).thenReturn("trace-1");
        given(userCredencialRepository.existsByUsername("usuario@correo.com")).willReturn(false);
        given(passwordEncoder.encode("clave123")).willReturn("hash-encriptado");
        given(userCredencialRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearUserCredencial(dtoValido());

        then(userCredencialRepository).should().save(any());
    }

    @Test
    void encontrarUserCredencialId_cuandoNoExiste_lanzaEntityNotFoundException() {
        lenient().when(request.getHeader("X-Trace-Id")).thenReturn("trace-1");
        given(userCredencialRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.encontrarUserCredencialId(1L));
    }

    @Test
    void actualizarPorUserId_cuandoNuevoUsernameYaPertenceAOtroUsuario_lanzaEntityConflictException() {
        lenient().when(request.getHeader("X-Trace-Id")).thenReturn("trace-1");
        UserCredencial existente = new UserCredencial();
        existente.setId(1L);
        existente.setUsername("antiguo@correo.com");

        ActualizarUsernameDTO dto = new ActualizarUsernameDTO();
        dto.setUsername("nuevo@correo.com");

        given(userCredencialRepository.findById(1L)).willReturn(Optional.of(existente));
        given(userCredencialRepository.existsByUsername("nuevo@correo.com")).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.actualizarPorUserId(1L, dto));
    }

    @Test
    void eliminarUserCredencial_cuandoNoExiste_lanzaEntityNotFoundException() {
        lenient().when(request.getHeader("X-Trace-Id")).thenReturn("trace-1");
        given(userCredencialRepository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarUserCredencial(1L));
        then(userCredencialRepository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
