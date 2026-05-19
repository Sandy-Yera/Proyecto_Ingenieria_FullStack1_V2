package com.logistica.ms_buildings.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logistica.ms_buildings.exception.entity.EntityBadRequestException;
import com.logistica.ms_buildings.exception.entity.EntityConflictException;
import com.logistica.ms_buildings.model.Edificio;
import com.logistica.ms_buildings.repository.EdificioRepository; // Nota: Asegúrate de crear esta interfaz básica de JpaRepository

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EdificioService {

    private final EdificioRepository edificioRepository;

    // CREAR
    public Edificio crearEdificio(Edificio edificio) {
        if (edificio.getId() != null && edificioRepository.existsById(edificio.getId())) {
            throw new EntityConflictException("Ya existe un edificio con este ID");
        }
        return edificioRepository.save(edificio);
    }

    // LEER
    public List<Edificio> listarEdificios() {
        return edificioRepository.findAll();
    }
    
    // ACTUALIZAR
    @Transactional
    public Edificio actualizarEdificio(Long id, Edificio edificio) {
        Edificio edificioExistente = edificioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. El edificio con ID " + id + " no existe."));

        if (edificio.getId() != null && !edificio.getId().equals(id)) {
            throw new EntityBadRequestException("El id ingresado y el del edificio no coinciden");
        }
                
        // Actualizar datos
        edificioExistente.setNombreEdificio(edificio.getNombreEdificio());
        edificioExistente.setDireccion(edificio.getDireccion());
        edificioExistente.setComuna(edificio.getComuna());
        edificioExistente.setNombreAdministrador(edificio.getNombreAdministrador());
        edificioExistente.setRutAdministrador(edificio.getRutAdministrador());
        edificioExistente.setTelefonoConserjeria(edificio.getTelefonoConserjeria());
        edificioExistente.setTotalDepartamentos(edificio.getTotalDepartamentos());
        edificioExistente.setLatitud(edificio.getLatitud());
        edificioExistente.setLongitud(edificio.getLongitud());

        return edificioExistente;
    }

    // ELIMINAR
    @Transactional
    public void eliminarEdificio(Long id) {
        if (!edificioRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el edificio a eliminar.");
        }
        edificioRepository.deleteById(id);
    }
}