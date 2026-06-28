package com.logistica.ms_logistics.service;

import com.logistica.ms_logistics.client.FleetClient;
import com.logistica.ms_logistics.client.WorkOrdersClient;
import com.logistica.ms_logistics.dto.AsignacionRequestDTO;
import com.logistica.ms_logistics.dto.AsignacionResponseDTO;
import com.logistica.ms_logistics.dto.CambioEstadoRequestDTO;
import com.logistica.ms_logistics.dto.feign.OrdenTrabajoFeignDTO;
import com.logistica.ms_logistics.exception.entity.EntityBadRequestException;
import com.logistica.ms_logistics.exception.entity.EntityConflictException;
import com.logistica.ms_logistics.exception.entity.EntityNotFoundException;
import com.logistica.ms_logistics.model.AsignacionLogistica;
import com.logistica.ms_logistics.model.EstadoLogistica;
import com.logistica.ms_logistics.repository.AsignacionLogisticaRepository;
import com.logistica.ms_logistics.service.impl.AsignacionLogisticaServiceImpl;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AsignacionLogisticaServiceImplTest {

    @Mock
    AsignacionLogisticaRepository repository;
    @Mock
    WorkOrdersClient workOrdersClient;
    @Mock
    FleetClient fleetClient;
    @Mock
    AsignacionLogisticaService self;

    AsignacionLogisticaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AsignacionLogisticaServiceImpl(repository, workOrdersClient, fleetClient, self);
    }

    private static FeignException.NotFound notFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(),
                null, new RequestTemplate());
        return new FeignException.NotFound("not found", request, null, null);
    }

    private static AsignacionRequestDTO dtoValido() {
        return new AsignacionRequestDTO(1L, 10L,
                -33.4569, -70.6483, -33.4372, -70.6506, LocalDateTime.now());
    }

    @Test
    void crearAsignacion_cuandoOrdenNoExiste_lanzaEntityNotFoundException() {
        given(workOrdersClient.obtenerOrden(1L)).willThrow(notFound());

        assertThrows(EntityNotFoundException.class, () -> service.crearAsignacion(dtoValido()));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void crearAsignacion_cuandoOrdenNoEsAssigned_lanzaEntityBadRequestException() {
        OrdenTrabajoFeignDTO orden = new OrdenTrabajoFeignDTO(1L, 5L, "PENDING", 1L);
        given(workOrdersClient.obtenerOrden(1L)).willReturn(orden);

        assertThrows(EntityBadRequestException.class, () -> service.crearAsignacion(dtoValido()));
        then(repository).shouldHaveNoInteractions();
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void crearAsignacion_cuandoYaExisteAsignacionActiva_lanzaEntityConflictException() {
        OrdenTrabajoFeignDTO orden = new OrdenTrabajoFeignDTO(1L, 5L, "ASSIGNED", 1L);
        given(workOrdersClient.obtenerOrden(1L)).willReturn(orden);
        given(repository.existsByOrdenTrabajoIdAndEstadoNot(1L, EstadoLogistica.CANCELADO)).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearAsignacion(dtoValido()));
        then(fleetClient).shouldHaveNoInteractions();
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void crearAsignacion_cuandoDatosValidos_calculaHaversineYGuardaConTecnicoIdCopiado() {
        OrdenTrabajoFeignDTO orden = new OrdenTrabajoFeignDTO(1L, 5L, "ASSIGNED", 1L);
        given(workOrdersClient.obtenerOrden(1L)).willReturn(orden);
        given(repository.existsByOrdenTrabajoIdAndEstadoNot(1L, EstadoLogistica.CANCELADO)).willReturn(false);
        given(self.guardarTransaccional(any())).willReturn(new AsignacionResponseDTO());

        service.crearAsignacion(dtoValido());

        then(self).should().guardarTransaccional(argThat(a ->
                a.getTecnicoId().equals(5L)
                        && a.getDistanciaKm() != null
                        && a.getDistanciaKm() > 0
                        && a.getTiempoEstimadoMinutos() != null));
    }

    @Test
    void enRuta_cuandoEstadoNoEsPlanificado_lanzaEntityBadRequestException() {
        AsignacionLogistica asignacion = new AsignacionLogistica();
        asignacion.setEstado(EstadoLogistica.EN_RUTA);
        given(repository.findById(1L)).willReturn(Optional.of(asignacion));

        assertThrows(EntityBadRequestException.class, () -> service.enRuta(1L));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void completar_cuandoEstadoNoEsEnRuta_lanzaEntityBadRequestException() {
        AsignacionLogistica asignacion = new AsignacionLogistica();
        asignacion.setEstado(EstadoLogistica.PLANIFICADO);
        given(repository.findById(1L)).willReturn(Optional.of(asignacion));

        assertThrows(EntityBadRequestException.class,
                () -> service.completar(1L, new CambioEstadoRequestDTO("ok")));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void cancelar_cuandoEstadoEsCompletado_lanzaEntityConflictException() {
        AsignacionLogistica asignacion = new AsignacionLogistica();
        asignacion.setEstado(EstadoLogistica.COMPLETADO);
        given(repository.findById(1L)).willReturn(Optional.of(asignacion));

        assertThrows(EntityConflictException.class,
                () -> service.cancelar(1L, new CambioEstadoRequestDTO("motivo")));
        then(self).shouldHaveNoInteractions();
    }
}
