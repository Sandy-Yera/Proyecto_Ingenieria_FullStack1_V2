package com.logistica.ms_security.service;

import com.logistica.ms_security.dto.RoleRequestDTO;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.Role;
import com.logistica.ms_security.repository.RoleRepository;
import com.logistica.ms_security.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleServiceImpl service;

    private static RoleRequestDTO dtoValido() {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRolName("ADMIN");
        dto.setJsonPermissions("[\"READ\",\"WRITE\"]");
        return dto;
    }

    @Test
    void crearRole_cuandoDatosValidos_guardaElRol() {
        given(roleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearRole(dtoValido());

        then(roleRepository).should().save(any());
    }

    @Test
    void actualizarRole_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(roleRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.actualizarRole(1L, dtoValido()));
    }

    @Test
    void actualizarRole_cuandoExiste_actualizaElNombre() {
        Role existente = new Role();
        existente.setId(1L);
        existente.setRolName("VIEJO_NOMBRE");
        given(roleRepository.findById(1L)).willReturn(Optional.of(existente));

        service.actualizarRole(1L, dtoValido());

        org.junit.jupiter.api.Assertions.assertEquals("ADMIN", existente.getRolName());
    }

    @Test
    void eliminarRole_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(roleRepository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarRole(1L));
        then(roleRepository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
