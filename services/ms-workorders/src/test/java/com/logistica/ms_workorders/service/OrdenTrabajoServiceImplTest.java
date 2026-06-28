package com.logistica.ms_workorders.service;

import com.logistica.ms_workorders.client.BuildingsClient;
import com.logistica.ms_workorders.client.QuotesClient;
import com.logistica.ms_workorders.client.ScheduleClient;
import com.logistica.ms_workorders.client.UsersClient;
import com.logistica.ms_workorders.dto.AsignarTecnicoRequestDTO;
import com.logistica.ms_workorders.dto.CambioEstadoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoRequestDTO;
import com.logistica.ms_workorders.dto.OrdenTrabajoResponseDTO;
import com.logistica.ms_workorders.exception.entity.EntityBadRequestException;
import com.logistica.ms_workorders.exception.entity.EntityConflictException;
import com.logistica.ms_workorders.exception.entity.EntityNotFoundException;
import com.logistica.ms_workorders.model.Categoria;
import com.logistica.ms_workorders.model.EstadoOrden;
import com.logistica.ms_workorders.model.OrdenTrabajo;
import com.logistica.ms_workorders.repository.OrdenTrabajoRepository;
import com.logistica.ms_workorders.service.impl.OrdenTrabajoServiceImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrdenTrabajoServiceImplTest {

    @Mock
    OrdenTrabajoRepository repository;
    @Mock
    UsersClient usersClient;
    @Mock
    BuildingsClient buildingsClient;
    @Mock
    QuotesClient quotesClient;
    @Mock
    ScheduleClient scheduleClient;
    @Mock
    OrdenTrabajoService self;

    OrdenTrabajoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrdenTrabajoServiceImpl(
                repository, usersClient, buildingsClient, quotesClient, scheduleClient, self);
    }

    private static FeignException.NotFound notFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(),
                null, new RequestTemplate());
        return new FeignException.NotFound("not found", request, null, null);
    }

    @Test
    void crearOrden_cuandoEdificioNoExiste_lanzaEntityNotFoundException() {
        OrdenTrabajoRequestDTO dto = new OrdenTrabajoRequestDTO(1L, null, "Fuga de agua", Categoria.PLOMERIA);
        given(buildingsClient.obtenerEdificioPorId(1L)).willThrow(notFound());

        assertThrows(EntityNotFoundException.class, () -> service.crearOrden(dto));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void crearOrden_cuandoEdificioEsValido_llamaGuardarOrdenTransaccionalConEstadoPending() {
        OrdenTrabajoRequestDTO dto = new OrdenTrabajoRequestDTO(1L, null, "Fuga de agua", Categoria.PLOMERIA);
        given(self.guardarOrdenTransaccional(any())).willReturn(new OrdenTrabajoResponseDTO());

        service.crearOrden(dto);

        then(self).should().guardarOrdenTransaccional(
                org.mockito.ArgumentMatchers.argThat(o -> o.getEstado() == null
                        && o.getBuildingId().equals(1L)
                        && o.getCategoria() == Categoria.PLOMERIA));
    }

    @Test
    void asignarTecnico_cuandoOrdenNoEsPending_lanzaEntityBadRequestException() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstado(EstadoOrden.ASSIGNED);
        given(repository.findById(1L)).willReturn(Optional.of(orden));

        AsignarTecnicoRequestDTO dto = new AsignarTecnicoRequestDTO(
                2L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        assertThrows(EntityBadRequestException.class,
                () -> service.asignarTecnico(1L, dto));
        then(scheduleClient).shouldHaveNoInteractions();
    }

    @Test
    void asignarTecnico_cuandoOrdenPendingYDatosValidos_creaBloqueYActualizaEstadoAssigned() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstado(EstadoOrden.PENDING);
        given(repository.findById(1L)).willReturn(Optional.of(orden));
        given(self.guardarOrdenTransaccional(any())).willReturn(new OrdenTrabajoResponseDTO());

        AsignarTecnicoRequestDTO dto = new AsignarTecnicoRequestDTO(
                2L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        service.asignarTecnico(1L, dto);

        then(scheduleClient).should().crearBloque(any());
        then(self).should().guardarOrdenTransaccional(
                org.mockito.ArgumentMatchers.argThat(o -> o.getEstado() == EstadoOrden.ASSIGNED
                        && o.getTecnicoId().equals(2L)));
    }

    @Test
    void iniciarTrabajo_cuandoOrdenNoEsAssigned_lanzaEntityBadRequestException() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstado(EstadoOrden.PENDING);
        given(repository.findById(1L)).willReturn(Optional.of(orden));

        assertThrows(EntityBadRequestException.class, () -> service.iniciarTrabajo(1L));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void completarTrabajo_cuandoOrdenNoEsInProgress_lanzaEntityBadRequestException() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstado(EstadoOrden.ASSIGNED);
        given(repository.findById(1L)).willReturn(Optional.of(orden));

        assertThrows(EntityBadRequestException.class,
                () -> service.completarTrabajo(1L, new CambioEstadoRequestDTO("listo")));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void cancelarOrden_cuandoOrdenEsCompleted_lanzaEntityConflictException() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstado(EstadoOrden.COMPLETED);
        given(repository.findById(1L)).willReturn(Optional.of(orden));

        assertThrows(EntityConflictException.class,
                () -> service.cancelarOrden(1L, new CambioEstadoRequestDTO("motivo")));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void eliminarOrden_cuandoOrdenEsInProgress_lanzaEntityConflictException() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstado(EstadoOrden.IN_PROGRESS);
        given(repository.findById(1L)).willReturn(Optional.of(orden));

        assertThrows(EntityConflictException.class, () -> service.eliminarOrden(1L));
        then(repository).should(org.mockito.Mockito.never()).deleteById(eq(1L));
    }
}
