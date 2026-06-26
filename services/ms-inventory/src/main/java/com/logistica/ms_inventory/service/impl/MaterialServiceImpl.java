package com.logistica.ms_inventory.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.ms_inventory.dto.MaterialRequestDTO;
import com.logistica.ms_inventory.dto.MaterialResponseDTO;
import com.logistica.ms_inventory.exception.entity.EntityBadRequestException;
import com.logistica.ms_inventory.exception.entity.EntityConflictException;
import com.logistica.ms_inventory.exception.entity.EntityNotFoundException;
import com.logistica.ms_inventory.model.Material;
import com.logistica.ms_inventory.repository.MaterialRepository;
import com.logistica.ms_inventory.service.MaterialService;

@Service
@Transactional(readOnly = true)
public class MaterialServiceImpl implements MaterialService {

    private static final Logger log = LoggerFactory.getLogger(MaterialServiceImpl.class);

    private final MaterialRepository materialRepository;

    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    @Transactional
    public MaterialResponseDTO crearMaterial(MaterialRequestDTO dto) {
        if (materialRepository.existsByNombre(dto.getNombre())) {
            throw new EntityConflictException("Ya existe un material con el nombre: " + dto.getNombre());
        }
        Material guardado = materialRepository.save(mapToEntity(dto));
        log.info("[ms-inventory] Material creado id={} nombre={}", guardado.getId(), guardado.getNombre());
        return mapToResponseDTO(guardado);
    }

    @Override
    public List<MaterialResponseDTO> listarMateriales() {
        List<MaterialResponseDTO> lista = materialRepository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        log.info("[ms-inventory] Consulta todos los materiales. Total: {}", lista.size());
        return lista;
    }

    @Override
    public MaterialResponseDTO obtenerMaterialPorId(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ms-inventory] Material no encontrado id={}", id);
                    return new EntityNotFoundException("Material no encontrado con ID: " + id);
                });
        return mapToResponseDTO(material);
    }

    @Override
    @Transactional
    public MaterialResponseDTO actualizarMaterial(Long id, MaterialRequestDTO dto) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se puede actualizar. Material con ID " + id + " no existe."));

        if (materialRepository.existsByNombreAndIdNot(dto.getNombre(), id)) {
            throw new EntityConflictException("Ya existe otro material con el nombre: " + dto.getNombre());
        }

        material.setNombre(dto.getNombre());
        material.setDescripcion(dto.getDescripcion());
        material.setStock(dto.getStock());
        material.setUnidad(dto.getUnidad());

        log.info("[ms-inventory] Material actualizado id={}", id);
        return mapToResponseDTO(material);
    }

    @Override
    @Transactional
    public void eliminarMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "No se puede eliminar. Material con ID " + id + " no existe.");
        }
        materialRepository.deleteById(id);
        log.info("[ms-inventory] Material eliminado id={}", id);
    }

    @Override
    @Transactional
    public MaterialResponseDTO consumirStock(Long id, Integer cantidad) {
        if (cantidad <= 0) {
            throw new EntityBadRequestException("La cantidad a consumir debe ser mayor a 0");
        }
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con ID: " + id));

        if (material.getStock() < cantidad) {
            throw new EntityBadRequestException(
                    "Stock insuficiente. Disponible: " + material.getStock() + ", solicitado: " + cantidad);
        }
        material.setStock(material.getStock() - cantidad);
        log.info("[ms-inventory] Consumo: material id={}, consumido={}, stock restante={}",
                id, cantidad, material.getStock());
        return mapToResponseDTO(material);
    }

    @Override
    @Transactional
    public MaterialResponseDTO reabastecerStock(Long id, Integer cantidad) {
        if (cantidad <= 0) {
            throw new EntityBadRequestException("La cantidad a reabastecer debe ser mayor a 0");
        }
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con ID: " + id));

        material.setStock(material.getStock() + cantidad);
        log.info("[ms-inventory] Reabastecimiento: material id={}, agregado={}, stock actual={}",
                id, cantidad, material.getStock());
        return mapToResponseDTO(material);
    }

    private Material mapToEntity(MaterialRequestDTO dto) {
        Material m = new Material();
        m.setNombre(dto.getNombre());
        m.setDescripcion(dto.getDescripcion());
        m.setStock(dto.getStock());
        m.setUnidad(dto.getUnidad());
        return m;
    }

    private MaterialResponseDTO mapToResponseDTO(Material m) {
        return new MaterialResponseDTO(
                m.getId(), m.getNombre(), m.getDescripcion(),
                m.getStock(), m.getUnidad(), m.getCreatedAt());
    }
}
