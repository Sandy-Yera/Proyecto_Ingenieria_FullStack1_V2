package com.logistica.ms_buildings.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_buildings.dto.EdificioRequestDTO;  // 🟢 Import Request DTO
import com.logistica.ms_buildings.dto.EdificioResponseDTO; // 🟢 Import Response DTO
import com.logistica.ms_buildings.exception.entity.EntityBadRequestException;
import com.logistica.ms_buildings.exception.entity.EntityConflictException;
import com.logistica.ms_buildings.exception.entity.EntityNotFoundException;
import com.logistica.ms_buildings.model.Edificio;
import com.logistica.ms_buildings.repository.EdificioRepository; 

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 🟢 Mantiene la optimización transaccional de la Etapa 2
public class EdificioService {

    private final EdificioRepository edificioRepository;

    // CREAR
    @Transactional
    public EdificioResponseDTO crearEdificio(EdificioRequestDTO dto) {
        // Mapeo manual de Request DTO a Entidad
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

        Edificio guardado = edificioRepository.save(edificio);
        return convertToResponseDTO(guardado);
    }

    // LEER
    public List<EdificioResponseDTO> listarEdificios() {
        return edificioRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // ACTUALIZAR
    @Transactional
    public EdificioResponseDTO actualizarEdificio(Long id, EdificioRequestDTO dto) {
        Edificio edificioExistente = edificioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. El edificio con ID " + id + " no existe."));
                
        // Sincronización limpia por Dirty Checking con los datos controlados del DTO
        edificioExistente.setNombreEdificio(dto.getNombreEdificio());
        edificioExistente.setDireccion(dto.getDireccion());
        edificioExistente.setComuna(dto.getComuna());
        edificioExistente.setNombreAdministrador(dto.getNombreAdministrador());
        edificioExistente.setRutAdministrador(dto.getRutAdministrador());
        edificioExistente.setTelefonoConserjeria(dto.getTelefonoConserjeria());
        edificioExistente.setTotalDepartamentos(dto.getTotalDepartamentos());
        edificioExistente.setLatitud(dto.getLatitud());
        edificioExistente.setLongitud(dto.getLongitud());

        return convertToResponseDTO(edificioExistente);
    }

    // ELIMINAR
    @Transactional
    public void eliminarEdificio(Long id) {
        if (!edificioRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el edificio a eliminar.");
        }
        edificioRepository.deleteById(id);
    }

    // 🟢 MÉTODOS AUXILIARES DE CONVERSIÓN (Mappers manuales limpios)
    private EdificioResponseDTO convertToResponseDTO(Edificio edificio) {
        EdificioResponseDTO dto = new EdificioResponseDTO();
        dto.setId(edificio.getId());
        dto.setNombreEdificio(edificio.getNombreEdificio());
        dto.setDireccion(edificio.getDireccion());
        dto.setComuna(edificio.getComuna());
        dto.setNombreAdministrador(edificio.getNombreAdministrador());
        dto.setRutAdministrador(edificio.getRutAdministrador());
        dto.setTelefonoConserjeria(edificio.getTelefonoConserjeria());
        dto.setTotalDepartamentos(edificio.getTotalDepartamentos());
        dto.setLatitud(edificio.getLatitud());
        dto.setLongitud(edificio.getLongitud());
        return dto;
    }
}