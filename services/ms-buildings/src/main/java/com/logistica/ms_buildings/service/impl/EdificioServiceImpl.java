package com.logistica.ms_buildings.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.logistica.ms_buildings.dto.EdificioRequestDTO;
import com.logistica.ms_buildings.dto.EdificioResponseDTO;
import com.logistica.ms_buildings.exception.entity.EntityBadRequestException;
import com.logistica.ms_buildings.exception.entity.EntityConflictException;
import com.logistica.ms_buildings.exception.entity.EntityCreationException;
import com.logistica.ms_buildings.exception.entity.EntityNotFoundException;
import com.logistica.ms_buildings.model.Edificio;
import com.logistica.ms_buildings.repository.EdificioRepository;
import com.logistica.ms_buildings.service.EdificioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * IMPLEMENTACIÓN DEL SERVICIO — EdificioServiceImpl
 * Contiene toda la lógica de negocio del dominio de Edificios.
 * Orquesta la interacción con el repositorio y aplica las reglas de validación del dominio.
 * El Controller no conoce esta clase directamente; inyecta la interfaz EdificioService.
 */
@Service
@RequiredArgsConstructor
public class EdificioServiceImpl implements EdificioService {

    private final EdificioRepository edificioRepository;

    // ============================================================
    //  CREAR
    // ============================================================
    @Override
    public EdificioResponseDTO crearEdificio(EdificioRequestDTO dto) {
        // Regla de negocio: RUT del administrador debe ser único en el sistema
        if (edificioRepository.existsByRutAdministrador(dto.getRutAdministrador())) {
            throw new EntityConflictException(
                "Ya existe un edificio administrado por el RUT: " + dto.getRutAdministrador());
        }

        Edificio edificio = mapToEntity(dto);

        try {
            Edificio guardado = edificioRepository.save(edificio);
            return mapToResponseDTO(guardado);
        } catch (Exception ex) {
            throw new EntityCreationException(
                "Error al persistir el edificio. Detalle: " + ex.getMessage());
        }
    }

    // ============================================================
    //  LEER — Todos
    // ============================================================
    @Override
    public List<EdificioResponseDTO> listarEdificios() {
        return edificioRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    //  LEER — Por ID
    // ============================================================
    @Override
    public EdificioResponseDTO obtenerEdificioPorId(Long id) {
        Edificio edificio = edificioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "No se encontró el edificio con ID: " + id));
        return mapToResponseDTO(edificio);
    }

    // ============================================================
    //  ACTUALIZAR
    // ============================================================
    @Override
    @Transactional
    public EdificioResponseDTO actualizarEdificio(Long id, EdificioRequestDTO dto) {
        Edificio edificioExistente = edificioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                    "No se puede actualizar. El edificio con ID " + id + " no existe."));

        // Regla de negocio: el RUT actualizado no puede colisionar con otro edificio
        if (edificioRepository.existsByRutAdministradorAndIdNot(dto.getRutAdministrador(), id)) {
            throw new EntityConflictException(
                "El RUT " + dto.getRutAdministrador() + " ya está asociado a otro edificio.");
        }

        // Validación de coordenadas básica (regla adicional de dominio)
        if (dto.getLatitud() == null || dto.getLongitud() == null) {
            throw new EntityBadRequestException(
                "Las coordenadas GPS (latitud/longitud) son obligatorias para un edificio.");
        }

        // Aplicar cambios sobre la entidad administrada (Transactional)
        edificioExistente.setNombreEdificio(dto.getNombreEdificio());
        edificioExistente.setDireccion(dto.getDireccion());
        edificioExistente.setComuna(dto.getComuna());
        edificioExistente.setNombreAdministrador(dto.getNombreAdministrador());
        edificioExistente.setRutAdministrador(dto.getRutAdministrador());
        edificioExistente.setTelefonoConserjeria(dto.getTelefonoConserjeria());
        edificioExistente.setTotalDepartamentos(dto.getTotalDepartamentos());
        edificioExistente.setLatitud(dto.getLatitud());
        edificioExistente.setLongitud(dto.getLongitud());

        return mapToResponseDTO(edificioExistente);
    }

    // ============================================================
    //  ELIMINAR
    // ============================================================
    @Override
    @Transactional
    public void eliminarEdificio(Long id) {
        if (!edificioRepository.existsById(id)) {
            throw new EntityNotFoundException(
                "No se puede eliminar. El edificio con ID " + id + " no existe.");
        }
        edificioRepository.deleteById(id);
    }

    // ============================================================
    //  HELPERS DE MAPEO (privados — sin exposición al Controller)
    // ============================================================

    /**
     * Convierte un EdificioRequestDTO en una entidad Edificio lista para persistir.
     */
    private Edificio mapToEntity(EdificioRequestDTO dto) {
        Edificio edificio = new Edificio();
        edificio.setNombreEdificio(dto.getNombreEdificio());
        edificio.setDireccion(dto.getDireccion());
        edificio.setComuna(dto.getComuna());
        edificio.setNombreAdministrador(dto.getNombreAdministrador());
        edificio.setRutAdministrador(dto.getRutAdministrador());
        edificio.setTelefonoConserjeria(dto.getTelefonoConserjeria());
        edificio.setTotalDepartamentos(dto.getTotalDepartamentos());
        edificio.setLatitud(dto.getLatitud());
        edificio.setLongitud(dto.getLongitud());
        return edificio;
    }

    /**
     * Convierte una entidad Edificio en un EdificioResponseDTO para la respuesta HTTP.
     */
    private EdificioResponseDTO mapToResponseDTO(Edificio edificio) {
        return new EdificioResponseDTO(
            edificio.getId(),
            edificio.getNombreEdificio(),
            edificio.getDireccion(),
            edificio.getComuna(),
            edificio.getNombreAdministrador(),
            edificio.getRutAdministrador(),
            edificio.getTelefonoConserjeria(),
            edificio.getTotalDepartamentos(),
            edificio.getLatitud(),
            edificio.getLongitud()
        );
    }
}
