package com.logistica.ms_payments.service;

import com.logistica.ms_payments.client.BillingClient;
import com.logistica.ms_payments.dto.CambioEstadoRequestDTO;
import com.logistica.ms_payments.dto.PagoRequestDTO;
import com.logistica.ms_payments.dto.PagoResponseDTO;
import com.logistica.ms_payments.dto.feign.FacturaFeignDTO;
import com.logistica.ms_payments.exception.entity.EntityBadRequestException;
import com.logistica.ms_payments.exception.entity.EntityConflictException;
import com.logistica.ms_payments.model.EstadoPago;
import com.logistica.ms_payments.model.MetodoPago;
import com.logistica.ms_payments.model.Pago;
import com.logistica.ms_payments.repository.PagoRepository;
import com.logistica.ms_payments.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    PagoRepository repository;
    @Mock
    BillingClient billingClient;
    @Mock
    PagoService self;

    PagoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PagoServiceImpl(repository, billingClient, self);
    }

    @Test
    void procesarPago_cuandoFacturaNoEstaPendiente_lanzaEntityConflictException() {
        FacturaFeignDTO factura = new FacturaFeignDTO(1L, 50000.0, "PAGADA");
        given(billingClient.obtenerFactura(1L)).willReturn(factura);

        PagoRequestDTO dto = new PagoRequestDTO(1L, 50000.0, MetodoPago.TRANSFERENCIA, "ref-1");

        assertThrows(EntityConflictException.class, () -> service.procesarPago(dto));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void procesarPago_cuandoMontoNoCoincide_lanzaEntityBadRequestException() {
        FacturaFeignDTO factura = new FacturaFeignDTO(1L, 50000.0, "PENDIENTE");
        given(billingClient.obtenerFactura(1L)).willReturn(factura);

        PagoRequestDTO dto = new PagoRequestDTO(1L, 30000.0, MetodoPago.TRANSFERENCIA, "ref-1");

        assertThrows(EntityBadRequestException.class, () -> service.procesarPago(dto));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void procesarPago_cuandoDatosValidos_guardaTransaccionalYNotificaBilling() {
        FacturaFeignDTO factura = new FacturaFeignDTO(1L, 50000.0, "PENDIENTE");
        given(billingClient.obtenerFactura(1L)).willReturn(factura);
        PagoResponseDTO guardado = new PagoResponseDTO();
        guardado.setId(1L);
        given(self.guardarTransaccional(any())).willReturn(guardado);
        given(repository.findById(1L)).willReturn(Optional.of(new Pago()));

        PagoRequestDTO dto = new PagoRequestDTO(1L, 50000.0, MetodoPago.TRANSFERENCIA, "ref-1");
        service.procesarPago(dto);

        then(billingClient).should().pagarFactura(eq(1L), any());
    }

    @Test
    void marcarFallido_cuandoPagoNoEstaCompletado_lanzaEntityConflictException() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setEstado(EstadoPago.PROCESANDO);
        given(repository.findById(1L)).willReturn(Optional.of(pago));

        assertThrows(EntityConflictException.class,
                () -> service.marcarFallido(1L, new CambioEstadoRequestDTO("ok")));
        then(self).shouldHaveNoInteractions();
    }
}
