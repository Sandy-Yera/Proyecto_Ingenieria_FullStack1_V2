package com.logistica.ms_inventory.service;

import java.util.List;

import com.logistica.ms_inventory.dto.MaterialRequestDTO;
import com.logistica.ms_inventory.dto.MaterialResponseDTO;

public interface MaterialService {
    MaterialResponseDTO crearMaterial(MaterialRequestDTO dto);
    List<MaterialResponseDTO> listarMateriales();
    MaterialResponseDTO obtenerMaterialPorId(Long id);
    MaterialResponseDTO actualizarMaterial(Long id, MaterialRequestDTO dto);
    void eliminarMaterial(Long id);
    MaterialResponseDTO consumirStock(Long id, Integer cantidad);
    MaterialResponseDTO reabastecerStock(Long id, Integer cantidad);
}
