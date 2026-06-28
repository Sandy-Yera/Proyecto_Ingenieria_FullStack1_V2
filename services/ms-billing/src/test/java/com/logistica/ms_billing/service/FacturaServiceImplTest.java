package com.logistica.ms_billing.service;

import com.logistica.ms_billing.client.QuotesClient;
import com.logistica.ms_billing.client.WorkOrdersClient;
import com.logistica.ms_billing.dto.CambioEstadoRequestDTO;
import com.logistica.ms_billing.dto.FacturaRequestDTO;
import com.logistica.ms_billing.dto.FacturaResponseDTO;
import com.logistica.ms_billing.dto.feign.WorkOrderFeignDTO;
import com.logistica.ms_billing.exception.entity.EntityBadRequestException;
import com.logistica.ms_billing.exception.entity.EntityConflictException;
import com.logistica.ms_billing.model.EstadoFactura;
import com.logistica.ms_billing.model.Factura;
import com.logistica.ms_billing.repository.FacturaRepository;
import com.logistica.ms_billing.service.impl.FacturaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FacturaServiceImplTest {

    @Mock
    FacturaRepository repository;
    @Mock
    WorkOrdersClient workOrdersClient;
    @Mock
    QuotesClient quotesClient;
    @Mock
    FacturaService self;

    FacturaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FacturaServiceImpl(repository, workOrdersClient, quotesClient, self);
    }

    @Test
    void emitirFactura_cuandoOrdenNoEstaCompleted_lanzaEntityBadRequestException() {
        WorkOrderFeignDTO orden = new WorkOrderFeignDTO(1L, "IN_PROGRESS", 1L, 1L, null, "Reparación");
        given(workOrdersClient.obtenerOrden(1L)).willReturn(orden);

        FacturaRequestDTO dto = new FacturaRequestDTO(1L, 50000.0);

        assertThrows(EntityBadRequestException.class, () -> service.emitirFactura(dto));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void emitirFactura_cuandoDatosValidosConMontoManual_guardaTransaccional() {
        WorkOrderFeignDTO orden = new WorkOrderFeignDTO(1L, "COMPLETED", 1L, 1L, null, "Reparación");
        given(workOrdersClient.obtenerOrden(1L)).willReturn(orden);
        given(repository.findByWorkOrderId(1L)).willReturn(Optional.empty());
        given(self.guardarTransaccional(any())).willReturn(new FacturaResponseDTO());

        FacturaRequestDTO dto = new FacturaRequestDTO(1L, 50000.0);
        service.emitirFactura(dto);

        then(self).should().guardarTransaccional(any());
    }

    @Test
    void pagarFactura_cuandoFacturaNoEstaPendiente_lanzaEntityConflictException() {
        Factura factura = new Factura();
        factura.setId(1L);
        factura.setEstado(EstadoFactura.PAGADA);
        given(repository.findById(1L)).willReturn(Optional.of(factura));

        assertThrows(EntityConflictException.class,
                () -> service.pagarFactura(1L, new CambioEstadoRequestDTO("ok")));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void eliminarFactura_cuandoFacturaNoEstaPendiente_lanzaEntityConflictException() {
        Factura factura = new Factura();
        factura.setId(1L);
        factura.setEstado(EstadoFactura.PAGADA);
        given(repository.findById(1L)).willReturn(Optional.of(factura));

        assertThrows(EntityConflictException.class, () -> service.eliminarFactura(1L));
        then(repository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
