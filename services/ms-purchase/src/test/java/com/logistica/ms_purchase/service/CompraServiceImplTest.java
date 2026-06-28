package com.logistica.ms_purchase.service;

import com.logistica.ms_purchase.client.InventoryClient;
import com.logistica.ms_purchase.dto.CompraRequestDTO;
import com.logistica.ms_purchase.dto.CompraResponseDTO;
import com.logistica.ms_purchase.exception.entity.EntityNotFoundException;
import com.logistica.ms_purchase.repository.CompraRepository;
import com.logistica.ms_purchase.service.impl.CompraServiceImpl;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CompraServiceImplTest {

    @Mock
    CompraRepository compraRepository;
    @Mock
    InventoryClient inventoryClient;
    @Mock
    CompraService self;

    CompraServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CompraServiceImpl(compraRepository, inventoryClient, self);
    }

    private static FeignException.NotFound notFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(),
                null, new RequestTemplate());
        return new FeignException.NotFound("not found", request, null, null);
    }

    @Test
    void registrarCompra_cuandoMaterialNoExiste_lanzaEntityNotFoundException() {
        given(inventoryClient.obtenerMaterialPorId(1L)).willThrow(notFound());

        CompraRequestDTO dto = new CompraRequestDTO(1L, "Ferretería Central", 10, 500.0);

        assertThrows(EntityNotFoundException.class, () -> service.registrarCompra(dto));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void registrarCompra_cuandoDatosValidos_reabasteceStockDespuesDelSave() {
        CompraRequestDTO dto = new CompraRequestDTO(1L, "Ferretería Central", 10, 500.0);
        CompraResponseDTO respuesta = new CompraResponseDTO(1L, 1L, "Ferretería Central", 10, 500.0, 5000.0, null);
        given(self.guardarCompraTransaccional(any())).willReturn(respuesta);

        service.registrarCompra(dto);

        then(inventoryClient).should().reabastecerStock(1L, 10);
    }

    @Test
    void eliminarCompra_cuandoCompraNoExiste_lanzaEntityNotFoundException() {
        given(compraRepository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarCompra(1L));
        then(compraRepository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
