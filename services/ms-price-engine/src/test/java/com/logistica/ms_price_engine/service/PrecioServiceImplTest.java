package com.logistica.ms_price_engine.service;

import com.logistica.ms_price_engine.dto.PrecioRequestDTO;
import com.logistica.ms_price_engine.dto.PrecioResponseDTO;
import com.logistica.ms_price_engine.exception.entity.EntityNotFoundException;
import com.logistica.ms_price_engine.model.Categoria;
import com.logistica.ms_price_engine.model.Tarifa;
import com.logistica.ms_price_engine.repository.TarifaRepository;
import com.logistica.ms_price_engine.service.impl.PrecioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PrecioServiceImplTest {

    @Mock
    TarifaRepository tarifaRepository;

    @InjectMocks
    PrecioServiceImpl service;

    @Test
    void calcularPrecio_cuandoCategoriaPlomeria_calculaMontoTotalCorrectamente() {
        Tarifa tarifa = new Tarifa(1L, Categoria.PLOMERIA, 25000.0, 8000.0);
        given(tarifaRepository.findByCategoria(Categoria.PLOMERIA)).willReturn(Optional.of(tarifa));

        PrecioRequestDTO dto = new PrecioRequestDTO(Categoria.PLOMERIA, 2.0, 3);
        PrecioResponseDTO respuesta = service.calcularPrecio(dto);

        assertThat(respuesta.getMontoTotal()).isEqualTo(74000.0);
    }

    @Test
    void calcularPrecio_cuandoTarifaNoExiste_lanzaEntityNotFoundException() {
        given(tarifaRepository.findByCategoria(Categoria.PLOMERIA)).willReturn(Optional.empty());

        PrecioRequestDTO dto = new PrecioRequestDTO(Categoria.PLOMERIA, 2.0, 3);

        assertThrows(EntityNotFoundException.class, () -> service.calcularPrecio(dto));
    }
}
