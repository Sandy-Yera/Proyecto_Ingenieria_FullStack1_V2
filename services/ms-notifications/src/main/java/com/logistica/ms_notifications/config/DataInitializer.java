package com.logistica.ms_notifications.config;

import com.logistica.ms_notifications.model.Notificacion;
import com.logistica.ms_notifications.model.TipoNotificacion;
import com.logistica.ms_notifications.repository.NotificacionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final NotificacionRepository repository;

    public DataInitializer(NotificacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (repository.count() == 0) {
            Notificacion n1 = new Notificacion();
            n1.setTipo(TipoNotificacion.ALERTA_GENERAL);
            n1.setDestinatarioId(1L);
            n1.setMensaje("Sistema BRM iniciado correctamente.");
            n1.setOrigen("ms-notifications");

            Notificacion n2 = new Notificacion();
            n2.setTipo(TipoNotificacion.ALERTA_GENERAL);
            n2.setDestinatarioId(1L);
            n2.setMensaje("Bienvenido al sistema de notificaciones BRM.");
            n2.setOrigen("ms-notifications");

            repository.saveAll(List.of(n1, n2));
            System.out.println(">> DataInitializer ms-notifications: 2 notificaciones semilla creadas.");
        } else {
            System.out.println(">> DataInitializer ms-notifications: ya existen notificaciones. Se omite.");
        }
    }
}