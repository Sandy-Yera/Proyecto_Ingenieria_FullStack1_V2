package com.logistica.ms_schedule.service;

import com.logistica.ms_schedule.dto.BloqueHorarioRequestDTO;
import com.logistica.ms_schedule.exception.entity.EntityConflictException;
import com.logistica.ms_schedule.exception.entity.EntityNotFoundException;
import com.logistica.ms_schedule.repository.BloqueHorarioRepository;
import com.logistica.ms_schedule.service.impl.BloqueHorarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BloqueHorarioServiceImplTest {

    @Mock
    BloqueHorarioRepository repository;

    @InjectMocks
    BloqueHorarioServiceImpl service;

    @Test
    void crearBloque_cuandoHaySolapamiento_lanzaEntityConflictException() {
        BloqueHorarioRequestDTO dto = new BloqueHorarioRequestDTO(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Mantención");
        given(repository.existsSolapamiento(1L, dto.getInicio(), dto.getFin())).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearBloque(dto));
        then(repository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearBloque_cuandoNoHaySolapamiento_guardaElBloque() {
        BloqueHorarioRequestDTO dto = new BloqueHorarioRequestDTO(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Mantención");
        given(repository.existsSolapamiento(1L, dto.getInicio(), dto.getFin())).willReturn(false);
        given(repository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearBloque(dto);

        then(repository).should().save(any());
    }

    @Test
    void actualizarBloque_cuandoHaySolapamientoExcluyendoPropioId_lanzaEntityConflictException() {
        com.logistica.ms_schedule.model.BloqueHorario existente = new com.logistica.ms_schedule.model.BloqueHorario();
        existente.setId(1L);
        given(repository.findById(1L)).willReturn(Optional.of(existente));

        BloqueHorarioRequestDTO dto = new BloqueHorarioRequestDTO(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Mantención");
        given(repository.existsSolapamientoExcluyendoId(1L, dto.getInicio(), dto.getFin(), 1L)).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.actualizarBloque(1L, dto));
    }

    @Test
    void obtenerPorId_cuandoBloqueNoExiste_lanzaEntityNotFoundException() {
        given(repository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtenerBloquePorId(1L));
    }
}
