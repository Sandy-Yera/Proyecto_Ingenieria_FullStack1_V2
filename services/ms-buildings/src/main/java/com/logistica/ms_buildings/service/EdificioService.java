package com.logistica.ms_buildings.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🟢 Corrección: Import oficial de Spring Framework

import com.logistica.ms_buildings.exception.entity.EntityBadRequestException;
import com.logistica.ms_buildings.exception.entity.EntityConflictException;
import com.logistica.ms_buildings.exception.entity.EntityNotFoundException;
import com.logistica.ms_buildings.model.Edificio;
import com.logistica.ms_buildings.repository.EdificioRepository; 

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 🟢 Configura lectura optimizada por defecto (listarEdificios se beneficia de esto)
public class EdificioService {

    private final EdificioRepository edificioRepository;

    // CREAR
    @Transactional // 🟢 Activa transacción de escritura para persistencia segura
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
    @Transactional // 🟢 Sobrescribe el modo readOnly para permitir escritura y Dirty Checking nativo
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
    @Transactional // 🟢 Requerido para operaciones de borrado físico estructurado
    public void eliminarEdificio(Long id) {
        if (!edificioRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el edificio a eliminar.");
        }
        edificioRepository.deleteById(id);
    }
}