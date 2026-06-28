package com.logistica.ms_quotes.service;

import com.logistica.ms_quotes.client.BuildingClient;
import com.logistica.ms_quotes.client.PriceEngineClient;
import com.logistica.ms_quotes.client.UserClient;
import com.logistica.ms_quotes.dto.CotizacionRequestDTO;
import com.logistica.ms_quotes.dto.CotizacionResponseDTO;
import com.logistica.ms_quotes.dto.PrecioResponseDTO;
import com.logistica.ms_quotes.exception.entity.EntityNotFoundException;
import com.logistica.ms_quotes.model.Categoria;
import com.logistica.ms_quotes.repository.CotizacionRepository;
import com.logistica.ms_quotes.service.impl.CotizacionServiceImpl;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CotizacionServiceImplTest {

    @Mock
    CotizacionRepository cotizacionRepository;
    @Mock
    UserClient userClient;
    @Mock
    BuildingClient buildingClient;
    @Mock
    PriceEngineClient priceEngineClient;
    @Mock
    CotizacionService self;

    CotizacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CotizacionServiceImpl(
                cotizacionRepository, userClient, buildingClient, priceEngineClient, self);
    }

    private static FeignException.NotFound notFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(),
                null, new RequestTemplate());
        return new FeignException.NotFound("not found", request, null, null);
    }

    private static CotizacionRequestDTO dtoValido() {
        return new CotizacionRequestDTO(1L, 1L, "Reparación de fuga", Categoria.PLOMERIA, 2.0, 3, null, null);
    }

    @Test
    void crearCotizacion_cuandoUsuarioNoExiste_lanzaEntityNotFoundException() {
        given(userClient.obtenerUsuarioPorId(1L)).willThrow(notFound());

        assertThrows(EntityNotFoundException.class, () -> service.crearCotizacion(dtoValido()));
        then(buildingClient).shouldHaveNoInteractions();
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void crearCotizacion_cuandoEdificioNoExiste_lanzaEntityNotFoundException() {
        given(buildingClient.obtenerEdificioPorId(1L)).willThrow(notFound());

        assertThrows(EntityNotFoundException.class, () -> service.crearCotizacion(dtoValido()));
        then(priceEngineClient).shouldHaveNoInteractions();
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void crearCotizacion_cuandoTodoEsValido_calculaPrecioYGuardaTransaccional() {
        PrecioResponseDTO precio = new PrecioResponseDTO(Categoria.PLOMERIA, 2.0, 3, 50000.0, 24000.0, 74000.0);
        given(priceEngineClient.calcularPrecio(any())).willReturn(precio);
        given(self.guardarCotizacionTransaccional(any())).willReturn(new CotizacionResponseDTO());

        service.crearCotizacion(dtoValido());

        then(self).should().guardarCotizacionTransaccional(
                argThat(c -> c.getMontoEstimado() != null && c.getMontoEstimado().equals(74000.0)));
    }

    @Test
    void actualizarCotizacion_cuandoCotizacionNoExiste_lanzaEntityNotFoundException() {
        given(cotizacionRepository.findById(1L)).willReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.actualizarCotizacion(1L, dtoValido()));
    }

    @Test
    void eliminarCotizacion_cuandoCotizacionNoExiste_lanzaEntityNotFoundException() {
        given(cotizacionRepository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarCotizacion(1L));
        then(cotizacionRepository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
