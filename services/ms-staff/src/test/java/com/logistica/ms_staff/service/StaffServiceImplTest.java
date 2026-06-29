package com.logistica.ms_staff.service;

import com.logistica.ms_staff.dto.StaffRequestDTO;
import com.logistica.ms_staff.exception.entity.EntityBadRequestException;
import com.logistica.ms_staff.exception.entity.EntityConflictException;
import com.logistica.ms_staff.exception.entity.EntityNotFoundException;
import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.model.Experiencia;
import com.logistica.ms_staff.model.Staff;
import com.logistica.ms_staff.repository.StaffRepository;
import com.logistica.ms_staff.service.impl.StaffServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    StaffRepository staffRepository;

    @Mock
    KafkaLogProducer logProducer;

    @InjectMocks
    StaffServiceImpl service;

    private static StaffRequestDTO dtoValido() {
        return new StaffRequestDTO(1L, Especialidad.GASFITER, Experiencia.SENIOR, true, "SEC-123");
    }

    @Test
    void crearTecnico_cuandoCertificacionSecYaExiste_lanzaEntityConflictException() {
        lenient().when(staffRepository.existsById(1L)).thenReturn(false);
        given(staffRepository.existsByCertificacionSec("SEC-123")).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearTecnico(dtoValido()));
        then(staffRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearTecnico_cuandoIdYaExiste_lanzaEntityBadRequestException() {
        given(staffRepository.existsById(1L)).willReturn(true);

        assertThrows(EntityBadRequestException.class, () -> service.crearTecnico(dtoValido()));
        then(staffRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearTecnico_cuandoDatosValidos_guardaElTecnico() {
        given(staffRepository.existsById(1L)).willReturn(false);
        given(staffRepository.existsByCertificacionSec("SEC-123")).willReturn(false);
        given(staffRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearTecnico(dtoValido());

        then(staffRepository).should().save(any());
    }

    @Test
    void actualizarDisponibilidad_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(staffRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.actualizarDisponibilidad(1L, false));
    }

    @Test
    void actualizarDisponibilidad_cuandoExiste_actualizaYGuarda() {
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setEstadoDisponibilidad(true);
        given(staffRepository.findById(1L)).willReturn(Optional.of(staff));
        given(staffRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.actualizarDisponibilidad(1L, false);

        assertEquals(false, staff.getEstadoDisponibilidad());
    }

    @Test
    void obtenerTecnicosDisponiblesPorEspecialidad_delegaFiltroAlRepository() {
        given(staffRepository.findByEspecialidadAndEstadoDisponibilidadTrue(Especialidad.ELECTRICISTA))
                .willReturn(List.of());

        service.obtenerTecnicosDisponiblesPorEspecialidad(Especialidad.ELECTRICISTA);

        then(staffRepository).should().findByEspecialidadAndEstadoDisponibilidadTrue(eq(Especialidad.ELECTRICISTA));
    }
}
