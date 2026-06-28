package com.logistica.ms_notifications.service;

import com.logistica.ms_notifications.dto.NotificacionRequestDTO;
import com.logistica.ms_notifications.dto.NotificacionResponseDTO;
import com.logistica.ms_notifications.model.Notificacion;
import com.logistica.ms_notifications.model.TipoNotificacion;

import java.util.List;

public interface NotificacionService {
    NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto);
    NotificacionResponseDTO marcarLeida(Long id);
    void eliminarNotificacion(Long id);
    NotificacionResponseDTO guardarTransaccional(Notificacion notificacion);
    List<NotificacionResponseDTO> listarTodas();
    NotificacionResponseDTO obtenerPorId(Long id);
    List<NotificacionResponseDTO> listarPorDestinatario(Long destinatarioId);
    List<NotificacionResponseDTO> listarNoLeidas(Long destinatarioId);
    long contarNoLeidas(Long destinatarioId);
    List<NotificacionResponseDTO> listarPorTipo(TipoNotificacion tipo);
}