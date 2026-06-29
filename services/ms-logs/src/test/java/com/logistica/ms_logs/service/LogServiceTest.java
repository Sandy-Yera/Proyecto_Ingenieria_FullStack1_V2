package com.logistica.ms_logs.service;

import com.logistica.ms_logs.model.LogEntity;
import com.logistica.ms_logs.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    LogRepository logRepository;

    @InjectMocks
    LogService service;

    private static LogEntity logDeEjemplo() {
        LogEntity log = new LogEntity();
        log.setId(1L);
        log.setServiceName("ms-auth");
        log.setLevel("INFO");
        log.setMessage("Mensaje de prueba");
        log.setTimestamp(Instant.now());
        return log;
    }

    @Test
    void listarConPaginacion_sinFiltros_consultaConCadenasVacias() {
        given(logRepository.findByServiceNameContainingAndLevelContaining(eq(""), eq(""), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(logDeEjemplo())));

        Page<com.logistica.ms_logs.dto.LogResponseDTO> resultado = service.listarConPaginacion(null, null, 0, 50);

        assertEquals(1, resultado.getTotalElements());
        then(logRepository).should().findByServiceNameContainingAndLevelContaining(eq(""), eq(""), any(Pageable.class));
    }

    @Test
    void listarConPaginacion_conFiltrosConEspacios_lostrimmeaAntesDeConsultar() {
        given(logRepository.findByServiceNameContainingAndLevelContaining(any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(logDeEjemplo())));

        service.listarConPaginacion("  ms-auth  ", "  INFO  ", 0, 50);

        then(logRepository).should().findByServiceNameContainingAndLevelContaining(eq("ms-auth"), eq("INFO"), any(Pageable.class));
    }

    @Test
    void listarConPaginacion_construyePageableOrdenadoPorIdDescendente() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        given(logRepository.findByServiceNameContainingAndLevelContaining(any(), any(), captor.capture()))
                .willReturn(new PageImpl<>(List.of(logDeEjemplo())));

        service.listarConPaginacion(null, null, 2, 20);

        Pageable pageable = captor.getValue();
        assertEquals(PageRequest.of(2, 20).getPageNumber(), pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals(org.springframework.data.domain.Sort.by("id").descending(), pageable.getSort());
    }

    @Test
    void listarConPaginacion_convierteEntidadesADtoCorrectamente() {
        given(logRepository.findByServiceNameContainingAndLevelContaining(any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(logDeEjemplo())));

        Page<com.logistica.ms_logs.dto.LogResponseDTO> resultado = service.listarConPaginacion(null, null, 0, 50);

        com.logistica.ms_logs.dto.LogResponseDTO dto = resultado.getContent().get(0);
        assertEquals("ms-auth", dto.getServiceName());
        assertEquals("INFO", dto.getLevel());
    }
}
