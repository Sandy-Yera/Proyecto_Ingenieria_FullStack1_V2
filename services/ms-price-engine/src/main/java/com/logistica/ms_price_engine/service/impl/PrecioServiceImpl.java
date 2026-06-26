package com.logistica.ms_price_engine.service.impl;

import org.springframework.stereotype.Service;

import com.logistica.ms_price_engine.dto.PrecioRequestDTO;
import com.logistica.ms_price_engine.dto.PrecioResponseDTO;
import com.logistica.ms_price_engine.exception.entity.EntityNotFoundException;
import com.logistica.ms_price_engine.model.Tarifa;
import com.logistica.ms_price_engine.repository.TarifaRepository;
import com.logistica.ms_price_engine.service.PrecioService;

@Service
public class PrecioServiceImpl implements PrecioService {

    private final TarifaRepository tarifaRepository;

    public PrecioServiceImpl(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    @Override
    public PrecioResponseDTO calcularPrecio(PrecioRequestDTO dto) {
        Tarifa tarifa = tarifaRepository.findByCategoria(dto.getCategoria())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró una tarifa para la categoría: " + dto.getCategoria()));

        Double costoLaboral = tarifa.getCostoHoraTecnico() * dto.getHorasTrabajo();
        Double costoMateriales = tarifa.getCostoMaterialPorUnidad() * dto.getUnidadesMaterial();
        Double montoTotal = costoLaboral + costoMateriales;

        PrecioResponseDTO responseDTO = new PrecioResponseDTO();
        responseDTO.setCategoria(dto.getCategoria());
        responseDTO.setHorasTrabajo(dto.getHorasTrabajo());
        responseDTO.setUnidadesMaterial(dto.getUnidadesMaterial());
        responseDTO.setCostoLaboral(costoLaboral);
        responseDTO.setCostoMateriales(costoMateriales);
        responseDTO.setMontoTotal(montoTotal);

        return responseDTO;
    }
}
