package com.logistica.ms_staff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.logistica.ms_staff.dto.StaffRequestDTO;
import com.logistica.ms_staff.dto.StaffResponseDTO;
import com.logistica.ms_staff.exception.entity.EntityBadRequestException;
import com.logistica.ms_staff.exception.entity.EntityConflictException;
import com.logistica.ms_staff.exception.entity.EntityNotFoundException;
import com.logistica.ms_staff.model.Especialidad;
import com.logistica.ms_staff.model.Staff;
import com.logistica.ms_staff.repository.StaffRepository;
import com.logistica.ms_staff.service.KafkaLogProducer;
import com.logistica.ms_staff.service.StaffService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final KafkaLogProducer logProducer;

    @Override
    public StaffResponseDTO crearTecnico(StaffRequestDTO dto) {
        if (staffRepository.existsById(dto.getId())) {
            logProducer.sendLog("WARN", "Intento de crear técnico con ID duplicado: " + dto.getId());
            throw new EntityBadRequestException("Ya existe un técnico con el ID: " + dto.getId());
        }
        if (staffRepository.existsByCertificacionSec(dto.getCertificacionSec())) {
            logProducer.sendLog("WARN", "Intento de registrar certificación SEC duplicada: " + dto.getCertificacionSec());
            throw new EntityConflictException("La certificación SEC ya está registrada: " + dto.getCertificacionSec());
        }

        Staff staff = mapToEntity(dto);
        StaffResponseDTO response = mapToResponseDTO(staffRepository.save(staff));

        logProducer.sendLog("INFO", "Técnico creado exitosamente con ID: " + response.getId()
                + " | Especialidad: " + response.getEspecialidad());
        return response;
    }

    @Override
    public StaffResponseDTO obtenerTecnicoPorId(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Técnico no encontrado con ID: " + id);
                    return new EntityNotFoundException("Técnico no encontrado con ID: " + id);
                });
        return mapToResponseDTO(staff);
    }

    @Override
    public List<StaffResponseDTO> obtenerTodosTecnicos() {
        List<StaffResponseDTO> lista = staffRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        logProducer.sendLog("INFO", "Consulta de todos los técnicos. Total: " + lista.size());
        return lista;
    }

    @Override
    public List<StaffResponseDTO> obtenerTecnicosPorEspecialidad(Especialidad especialidad) {
        List<StaffResponseDTO> lista = staffRepository.findByEspecialidad(especialidad)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        logProducer.sendLog("INFO", "Consulta técnicos por especialidad: " + especialidad + " | Total: " + lista.size());
        return lista;
    }

    @Override
    public List<StaffResponseDTO> obtenerTecnicosDisponibles() {
        List<StaffResponseDTO> lista = staffRepository.findByEstadoDisponibilidadTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        logProducer.sendLog("INFO", "Consulta técnicos disponibles. Total: " + lista.size());
        return lista;
    }

    @Override
    public List<StaffResponseDTO> obtenerTecnicosDisponiblesPorEspecialidad(Especialidad especialidad) {
        List<StaffResponseDTO> lista = staffRepository.findByEspecialidadAndEstadoDisponibilidadTrue(especialidad)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        logProducer.sendLog("INFO", "Consulta técnicos disponibles por especialidad: " + especialidad
                + " | Total: " + lista.size());
        return lista;
    }

    @Override
    public StaffResponseDTO actualizarTecnico(Long id, StaffRequestDTO dto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Intento de actualizar técnico inexistente con ID: " + id);
                    return new EntityNotFoundException("Técnico no encontrado con ID: " + id);
                });

        staff.setEspecialidad(dto.getEspecialidad());
        staff.setNivelExperiencia(dto.getNivelExperiencia());
        staff.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
        staff.setCertificacionSec(dto.getCertificacionSec());

        StaffResponseDTO response = mapToResponseDTO(staffRepository.save(staff));
        logProducer.sendLog("INFO", "Técnico actualizado con ID: " + id);
        return response;
    }

    @Override
    public StaffResponseDTO actualizarDisponibilidad(Long id, Boolean disponibilidad) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Intento de actualizar disponibilidad de técnico inexistente con ID: " + id);
                    return new EntityNotFoundException("Técnico no encontrado con ID: " + id);
                });

        staff.setEstadoDisponibilidad(disponibilidad);
        StaffResponseDTO response = mapToResponseDTO(staffRepository.save(staff));

        logProducer.sendLog("INFO", "Disponibilidad del técnico ID: " + id + " actualizada a: " + disponibilidad);
        return response;
    }

    @Override
    public void eliminarTecnico(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    logProducer.sendLog("WARN", "Intento de eliminar técnico inexistente con ID: " + id);
                    return new EntityNotFoundException("Técnico no encontrado con ID: " + id);
                });

        staffRepository.delete(staff);
        logProducer.sendLog("INFO", "Técnico eliminado con ID: " + id);
    }

    // --- Mappers ---

    private Staff mapToEntity(StaffRequestDTO dto) {
        Staff staff = new Staff();
        staff.setId(dto.getId());
        staff.setEspecialidad(dto.getEspecialidad());
        staff.setNivelExperiencia(dto.getNivelExperiencia());
        staff.setEstadoDisponibilidad(dto.getEstadoDisponibilidad());
        staff.setCertificacionSec(dto.getCertificacionSec());
        return staff;
    }

    private StaffResponseDTO mapToResponseDTO(Staff staff) {
        return new StaffResponseDTO(
                staff.getId(),
                staff.getEspecialidad(),
                staff.getNivelExperiencia(),
                staff.getEstadoDisponibilidad(),
                staff.getCertificacionSec(),
                staff.getCreatedAt()
        );
    }
}