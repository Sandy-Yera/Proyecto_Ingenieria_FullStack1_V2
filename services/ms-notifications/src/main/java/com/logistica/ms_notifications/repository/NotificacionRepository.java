package com.logistica.ms_notifications.repository;

import com.logistica.ms_notifications.model.Notificacion;
import com.logistica.ms_notifications.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByDestinatarioId(Long destinatarioId);
    List<Notificacion> findByDestinatarioIdAndLeidaFalse(Long destinatarioId);
    List<Notificacion> findByTipo(TipoNotificacion tipo);
    long countByDestinatarioIdAndLeidaFalse(Long destinatarioId);
}