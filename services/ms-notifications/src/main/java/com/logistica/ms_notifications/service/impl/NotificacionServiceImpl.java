package com.logistica.ms_notifications.service.impl;

import com.logistica.ms_notifications.dto.NotificacionRequestDTO;
import com.logistica.ms_notifications.dto.NotificacionResponseDTO;
import com.logistica.ms_notifications.exception.entity.EntityNotFoundException;
import com.logistica.ms_notifications.model.Notificacion;
import com.logistica.ms_notifications.model.TipoNotificacion;
import com.logistica.ms_notifications.repository.NotificacionRepository;
import com.logistica.ms_notifications.service.NotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceImpl.class);

    private final NotificacionRepository repository;
    private final NotificacionService self;

    public NotificacionServiceImpl(NotificacionRepository repository,
            @Lazy NotificacionService self) {
        this.repository = repository;
        this.self = self;
    }

    @Override
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto) {
        log.info("[ms-notifications] Creando notificacion tipo={} destinatario={}",
                dto.getTipo(), dto.getDestinatarioId());

        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(dto.getTipo());
        notificacion.setDestinatarioId(dto.getDestinatarioId());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setOrigen(dto.getOrigen());
        notificacion.setMetadata(dto.getMetadata());

        return self.guardarTransaccional(notificacion);
    }

    @Override
    public NotificacionResponseDTO marcarLeida(Long id) {
        log.info("[ms-notifications] Marcando leida notificacion id={}", id);

        Notificacion notificacion = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notificación no encontrada con ID: " + id));

        notificacion.setLeida(true);

        return self.guardarTransaccional(notificacion);
    }

    @Override
    @Transactional
    public void eliminarNotificacion(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Notificación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
        log.info("[ms-notifications] Notificacion eliminada id={}", id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificacionResponseDTO guardarTransaccional(Notificacion notificacion) {
        return mapToResponseDTO(repository.save(notificacion));
    }

    @Override
    public List<NotificacionResponseDTO> listarTodas() {
        return repository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public NotificacionResponseDTO obtenerPorId(Long id) {
        return mapToResponseDTO(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notificación no encontrada con ID: " + id)));
    }

    @Override
    public List<NotificacionResponseDTO> listarPorDestinatario(Long destinatarioId) {
        return repository.findByDestinatarioId(destinatarioId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<NotificacionResponseDTO> listarNoLeidas(Long destinatarioId) {
        return repository.findByDestinatarioIdAndLeidaFalse(destinatarioId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public long contarNoLeidas(Long destinatarioId) {
        return repository.countByDestinatarioIdAndLeidaFalse(destinatarioId);
    }

    @Override
    public List<NotificacionResponseDTO> listarPorTipo(TipoNotificacion tipo) {
        return repository.findByTipo(tipo)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    private NotificacionResponseDTO mapToResponseDTO(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(), n.getTipo(), n.getDestinatarioId(), n.getMensaje(),
                n.getLeida(), n.getOrigen(), n.getMetadata(), n.getFechaCreacion());
    }
}