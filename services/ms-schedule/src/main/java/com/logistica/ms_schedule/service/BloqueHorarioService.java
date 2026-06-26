package com.logistica.ms_schedule.service;

import java.util.List;

import com.logistica.ms_schedule.dto.BloqueHorarioRequestDTO;
import com.logistica.ms_schedule.dto.BloqueHorarioResponseDTO;

public interface BloqueHorarioService {
    BloqueHorarioResponseDTO crearBloque(BloqueHorarioRequestDTO dto);
    List<BloqueHorarioResponseDTO> listarBloques();
    BloqueHorarioResponseDTO obtenerBloquePorId(Long id);
    List<BloqueHorarioResponseDTO> listarPorTecnico(Long tecnicoId);
    BloqueHorarioResponseDTO actualizarBloque(Long id, BloqueHorarioRequestDTO dto);
    void eliminarBloque(Long id);
}
