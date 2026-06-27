package com.logistica.ms_logistics.repository;

import com.logistica.ms_logistics.model.AsignacionLogistica;
import com.logistica.ms_logistics.model.EstadoLogistica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionLogisticaRepository extends JpaRepository<AsignacionLogistica, Long> {

    List<AsignacionLogistica> findByOrdenTrabajoId(Long ordenTrabajoId);

    List<AsignacionLogistica> findByVehiculoId(Long vehiculoId);

    List<AsignacionLogistica> findByTecnicoId(Long tecnicoId);

    List<AsignacionLogistica> findByEstado(EstadoLogistica estado);

    boolean existsByOrdenTrabajoIdAndEstadoNot(Long ordenTrabajoId, EstadoLogistica estado);
}
