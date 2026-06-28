package com.logistica.ms_notifications.service;

import com.logistica.ms_notifications.dto.NotificacionRequestDTO;
import com.logistica.ms_notifications.dto.NotificacionResponseDTO;
import com.logistica.ms_notifications.exception.entity.EntityNotFoundException;
import com.logistica.ms_notifications.model.TipoNotificacion;
import com.logistica.ms_notifications.repository.NotificacionRepository;
import com.logistica.ms_notifications.service.impl.NotificacionServiceImpl;
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
class NotificacionServiceImplTest {

    @Mock
    NotificacionRepository repository;
    @Mock
    NotificacionService self;

    NotificacionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NotificacionServiceImpl(repository, self);
    }

    @Test
    void crearNotificacion_cuandoDatosValidos_guardaTransaccional() {
        given(self.guardarTransaccional(any())).willReturn(new NotificacionResponseDTO());

        NotificacionRequestDTO dto = new NotificacionRequestDTO(
                TipoNotificacion.ORDEN_ASIGNADA, 1L, "Se le asignó una nueva orden", "ms-workorders", null);
        service.crearNotificacion(dto);

        then(self).should().guardarTransaccional(any());
    }

    @Test
    void marcarLeida_cuandoNotificacionNoExiste_lanzaEntityNotFoundException() {
        given(repository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.marcarLeida(1L));
        then(self).shouldHaveNoInteractions();
    }

    @Test
    void eliminarNotificacion_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(repository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarNotificacion(1L));
        then(repository).should(org.mockito.Mockito.never()).deleteById(any());
    }
}
