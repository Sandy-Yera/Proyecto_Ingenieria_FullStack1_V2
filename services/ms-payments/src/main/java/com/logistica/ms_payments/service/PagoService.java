package com.logistica.ms_payments.service;

import com.logistica.ms_payments.dto.CambioEstadoRequestDTO;
import com.logistica.ms_payments.dto.PagoRequestDTO;
import com.logistica.ms_payments.dto.PagoResponseDTO;
import com.logistica.ms_payments.model.EstadoPago;
import com.logistica.ms_payments.model.MetodoPago;
import com.logistica.ms_payments.model.Pago;

import java.util.List;

public interface PagoService {
    PagoResponseDTO procesarPago(PagoRequestDTO dto);
    PagoResponseDTO marcarFallido(Long id, CambioEstadoRequestDTO dto);
    PagoResponseDTO guardarTransaccional(Pago pago);
    List<PagoResponseDTO> listarTodos();
    PagoResponseDTO obtenerPorId(Long id);
    List<PagoResponseDTO> listarPorFactura(Long facturaId);
    List<PagoResponseDTO> listarPorEstado(EstadoPago estado);
    List<PagoResponseDTO> listarPorMetodo(MetodoPago metodo);
}