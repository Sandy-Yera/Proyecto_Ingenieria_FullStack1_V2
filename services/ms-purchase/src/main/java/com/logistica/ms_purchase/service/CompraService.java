package com.logistica.ms_purchase.service;

import java.util.List;

import com.logistica.ms_purchase.dto.CompraRequestDTO;
import com.logistica.ms_purchase.dto.CompraResponseDTO;
import com.logistica.ms_purchase.model.Compra;

public interface CompraService {
    CompraResponseDTO registrarCompra(CompraRequestDTO dto);
    CompraResponseDTO guardarCompraTransaccional(Compra compra);
    List<CompraResponseDTO> listarCompras();
    CompraResponseDTO obtenerCompraPorId(Long id);
    List<CompraResponseDTO> listarPorMaterial(Long materialId);
    CompraResponseDTO actualizarCompra(Long id, CompraRequestDTO dto);
    void eliminarCompra(Long id);
}
