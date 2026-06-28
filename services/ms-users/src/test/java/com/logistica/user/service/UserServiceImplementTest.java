package com.logistica.user.service;

import com.logistica.user.client.AuthClient;
import com.logistica.user.dto.UserRegisterDTO;
import com.logistica.user.exception.entity.EntityConflictException;
import com.logistica.user.exception.entity.EntityNotFoundException;
import com.logistica.user.model.User;
import com.logistica.user.repository.UserRepository;
import com.logistica.user.service.impl.UserServiceImplement;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplementTest {

    @Mock
    UserRepository userRepository;
    @Mock
    KafkaLogProducer logProducer;
    @Mock
    AuthClient authClient;
    @Mock
    HttpServletRequest request;
    @Mock
    KafkaUserEventProducer userEventProducer;

    UserServiceImplement service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new UserServiceImplement(userRepository, logProducer, authClient, request, userEventProducer);
        given(request.getHeader("X-Trace-Id")).willReturn("trace-1");
    }

    private static UserRegisterDTO dtoValido() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setRut("12345678-9");
        dto.setPNombre("Juan");
        dto.setApPat("Pérez");
        dto.setTelefono("+56912345678");
        dto.setCorreo("juan.perez@correo.com");
        dto.setPassword("clave123");
        return dto;
    }

    @Test
    void crearUser_cuandoRutYaExiste_lanzaEntityConflictException() {
        given(userRepository.existsByRut("12345678-9")).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearUser(dtoValido()));
        then(authClient).shouldHaveNoInteractions();
    }

    @Test
    void crearUser_cuandoDatosValidos_llamaGenerarCredencialesDespuesDelSave() {
        given(userRepository.existsByRut("12345678-9")).willReturn(false);
        given(userRepository.existsByCorreo("juan.perez@correo.com")).willReturn(false);
        User guardado = new User();
        guardado.setId(1L);
        guardado.setRut("12345678-9");
        guardado.setCorreo("juan.perez@correo.com");
        given(userRepository.saveAndFlush(any())).willReturn(guardado);

        service.crearUser(dtoValido());

        then(authClient).should().generarCredencialesRemotas(any());
    }

    @Test
    void eliminarUserId_cuandoUsuarioNoExiste_lanzaEntityNotFoundException() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.eliminarUserId(1L));
        then(userRepository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
