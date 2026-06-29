package com.logistica.ms_security.service;

import com.logistica.ms_security.dto.RoleAssignmentRequestDTO;
import com.logistica.ms_security.exception.entity.EntityBadRequestException;
import com.logistica.ms_security.exception.entity.EntityConflictException;
import com.logistica.ms_security.exception.entity.EntityNotFoundException;
import com.logistica.ms_security.model.RoleAssignment;
import com.logistica.ms_security.repository.RoleAssignmentRepository;
import com.logistica.ms_security.service.impl.RoleAssignmentServiceImpl;
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
class RoleAssignmentServiceImplTest {

    @Mock
    RoleAssignmentRepository roleAssignmentRepository;

    @InjectMocks
    RoleAssignmentServiceImpl service;

    private static RoleAssignmentRequestDTO dtoValido() {
        RoleAssignmentRequestDTO dto = new RoleAssignmentRequestDTO();
        dto.setIdUser(1L);
        dto.setIdRole(2L);
        return dto;
    }

    @Test
    void crearRoleAssignment_cuandoFaltaIdUserOIdRole_lanzaEntityBadRequestException() {
        RoleAssignmentRequestDTO dto = new RoleAssignmentRequestDTO();
        dto.setIdUser(null);
        dto.setIdRole(2L);

        assertThrows(EntityBadRequestException.class, () -> service.crearRoleAssignment(dto));
    }

    @Test
    void crearRoleAssignment_cuandoAsignacionYaExiste_lanzaEntityConflictException() {
        given(roleAssignmentRepository.findByIdUserAndIdRole(1L, 2L))
                .willReturn(Optional.of(new RoleAssignment()));

        assertThrows(EntityConflictException.class, () -> service.crearRoleAssignment(dtoValido()));
        then(roleAssignmentRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearRoleAssignment_cuandoDatosValidos_guardaLaAsignacion() {
        given(roleAssignmentRepository.findByIdUserAndIdRole(1L, 2L)).willReturn(Optional.empty());
        given(roleAssignmentRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearRoleAssignment(dtoValido());

        then(roleAssignmentRepository).should().save(any());
    }

    @Test
    void eliminarRoleAssignment_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(roleAssignmentRepository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarRoleAssignment(1L));
    }
}
