package com.logistica.ms_buildings.service;

import com.logistica.ms_buildings.dto.EdificioRequestDTO;
import com.logistica.ms_buildings.exception.entity.EntityConflictException;
import com.logistica.ms_buildings.exception.entity.EntityNotFoundException;
import com.logistica.ms_buildings.model.Edificio;
import com.logistica.ms_buildings.repository.EdificioRepository;
import com.logistica.ms_buildings.service.impl.EdificioServiceImpl;
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
class EdificioServiceImplTest {

    @Mock
    EdificioRepository edificioRepository;

    @InjectMocks
    EdificioServiceImpl service;

    private static EdificioRequestDTO dtoValido() {
        return new EdificioRequestDTO(
                "Edificio Las Torres", "Av. Siempre Viva 123", "Providencia",
                "Juan Pérez", "12345678-9", "+56912345678", 50, -33.45, -70.65);
    }

    @Test
    void crearEdificio_cuandoRutAdministradorYaExiste_lanzaEntityConflictException() {
        given(edificioRepository.existsByRutAdministrador("12345678-9")).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearEdificio(dtoValido()));
        then(edificioRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearEdificio_cuandoDatosValidos_guardaElEdificio() {
        given(edificioRepository.existsByRutAdministrador("12345678-9")).willReturn(false);
        given(edificioRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearEdificio(dtoValido());

        then(edificioRepository).should().save(any());
    }

    @Test
    void obtenerEdificioPorId_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(edificioRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtenerEdificioPorId(1L));
    }
}
