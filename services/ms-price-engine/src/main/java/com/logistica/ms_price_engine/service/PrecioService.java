package com.logistica.ms_price_engine.service;

import com.logistica.ms_price_engine.dto.PrecioRequestDTO;
import com.logistica.ms_price_engine.dto.PrecioResponseDTO;

public interface PrecioService {
    PrecioResponseDTO calcularPrecio(PrecioRequestDTO dto);
}
