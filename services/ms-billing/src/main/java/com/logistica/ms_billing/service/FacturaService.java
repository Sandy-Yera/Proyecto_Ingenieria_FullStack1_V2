package com.logistica.ms_billing.service;

import com.logistica.ms_billing.dto.CambioEstadoRequestDTO;
import com.logistica.ms_billing.dto.FacturaRequestDTO;
import com.logistica.ms_billing.dto.FacturaResponseDTO;
import com.logistica.ms_billing.model.EstadoFactura;
import com.logistica.ms_billing.model.Factura;

import java.util.List;

public interface FacturaService {
    FacturaResponseDTO emitirFactura(FacturaRequestDTO dto);
    FacturaResponseDTO pagarFactura(Long id, CambioEstadoRequestDTO dto);
    FacturaResponseDTO anularFactura(Long id, CambioEstadoRequestDTO dto);
    void eliminarFactura(Long id);
    FacturaResponseDTO guardarTransaccional(Factura factura);
    List<FacturaResponseDTO> listarTodas();
    FacturaResponseDTO obtenerPorId(Long id);
    List<FacturaResponseDTO> listarPorEstado(EstadoFactura estado);
    List<FacturaResponseDTO> listarPorWorkOrder(Long workOrderId);
    List<FacturaResponseDTO> listarPorTecnico(Long tecnicoId);
    List<FacturaResponseDTO> listarPorBuilding(Long buildingId);
}