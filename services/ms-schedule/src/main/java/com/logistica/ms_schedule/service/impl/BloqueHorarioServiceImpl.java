package com.logistica.ms_schedule.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_schedule.dto.BloqueHorarioRequestDTO;
import com.logistica.ms_schedule.dto.BloqueHorarioResponseDTO;
import com.logistica.ms_schedule.exception.entity.EntityBadRequestException;
import com.logistica.ms_schedule.exception.entity.EntityConflictException;
import com.logistica.ms_schedule.exception.entity.EntityNotFoundException;
import com.logistica.ms_schedule.model.BloqueHorario;
import com.logistica.ms_schedule.repository.BloqueHorarioRepository;
import com.logistica.ms_schedule.service.BloqueHorarioService;

/**
 * Implementación del servicio de Bloques Horarios.
 *
 * Lógica anti-solapamiento: antes de crear o actualizar un bloque, se verifica
 * que el mismo técnico no tenga un bloque que se solape. La condición de
 * solapamiento es: inicio1 < fin2 && fin1 > inicio2.
 */
@Service
@Transactional(readOnly = true)
public class BloqueHorarioServiceImpl implements BloqueHorarioService {

    private static final Logger log = LoggerFactory.getLogger(BloqueHorarioServiceImpl.class);

    private final BloqueHorarioRepository bloqueRepository;

    public BloqueHorarioServiceImpl(BloqueHorarioRepository bloqueRepository) {
        this.bloqueRepository = bloqueRepository;
    }

    @Override
    @Transactional
    public BloqueHorarioResponseDTO crearBloque(BloqueHorarioRequestDTO dto) {
        validarFechas(dto);

        if (bloqueRepository.existsSolapamiento(dto.getTecnicoId(), dto.getInicio(), dto.getFin())) {
            throw new EntityConflictException(
                    "El técnico con ID " + dto.getTecnicoId()
                            + " ya tiene un bloque horario que se solapa con el rango "
                            + dto.getInicio() + " - " + dto.getFin());
        }

        BloqueHorario guardado = bloqueRepository.save(mapToEntity(dto));
        log.info("[ms-schedule] Bloque creado id={} tecnicoId={}", guardado.getId(), guardado.getTecnicoId());
        return mapToResponseDTO(guardado);
    }

    @Override
    public List<BloqueHorarioResponseDTO> listarBloques() {
        List<BloqueHorarioResponseDTO> lista = bloqueRepository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        log.info("[ms-schedule] Consulta todos los bloques. Total: {}", lista.size());
        return lista;
    }

    @Override
    public BloqueHorarioResponseDTO obtenerBloquePorId(Long id) {
        BloqueHorario bloque = bloqueRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ms-schedule] Bloque no encontrado id={}", id);
                    return new EntityNotFoundException("Bloque horario no encontrado con ID: " + id);
                });
        return mapToResponseDTO(bloque);
    }

    @Override
    public List<BloqueHorarioResponseDTO> listarPorTecnico(Long tecnicoId) {
        List<BloqueHorarioResponseDTO> lista = bloqueRepository.findByTecnicoId(tecnicoId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        log.info("[ms-schedule] Consulta bloques por tecnicoId={}. Total: {}", tecnicoId, lista.size());
        return lista;
    }

    @Override
    @Transactional
    public BloqueHorarioResponseDTO actualizarBloque(Long id, BloqueHorarioRequestDTO dto) {
        BloqueHorario bloque = bloqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se puede actualizar. Bloque con ID " + id + " no existe."));

        validarFechas(dto);

        if (bloqueRepository.existsSolapamientoExcluyendoId(
                dto.getTecnicoId(), dto.getInicio(), dto.getFin(), id)) {
            throw new EntityConflictException(
                    "El técnico con ID " + dto.getTecnicoId()
                            + " ya tiene otro bloque que se solapa con el rango "
                            + dto.getInicio() + " - " + dto.getFin());
        }

        bloque.setTecnicoId(dto.getTecnicoId());
        bloque.setInicio(dto.getInicio());
        bloque.setFin(dto.getFin());
        bloque.setDescripcion(dto.getDescripcion());

        log.info("[ms-schedule] Bloque actualizado id={}", id);
        return mapToResponseDTO(bloque);
    }

    @Override
    @Transactional
    public void eliminarBloque(Long id) {
        if (!bloqueRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "No se puede eliminar. Bloque con ID " + id + " no existe.");
        }
        bloqueRepository.deleteById(id);
        log.info("[ms-schedule] Bloque eliminado id={}", id);
    }

    private void validarFechas(BloqueHorarioRequestDTO dto) {
        if (!dto.getFin().isAfter(dto.getInicio())) {
            throw new EntityBadRequestException(
                    "La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private BloqueHorario mapToEntity(BloqueHorarioRequestDTO dto) {
        BloqueHorario b = new BloqueHorario();
        b.setTecnicoId(dto.getTecnicoId());
        b.setInicio(dto.getInicio());
        b.setFin(dto.getFin());
        b.setDescripcion(dto.getDescripcion());
        return b;
    }

    private BloqueHorarioResponseDTO mapToResponseDTO(BloqueHorario b) {
        return new BloqueHorarioResponseDTO(
                b.getId(), b.getTecnicoId(), b.getInicio(),
                b.getFin(), b.getDescripcion(), b.getCreatedAt());
    }
}
