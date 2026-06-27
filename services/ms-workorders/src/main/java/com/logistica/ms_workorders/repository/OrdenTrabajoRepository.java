package com.logistica.ms_workorders.repository;

import com.logistica.ms_workorders.model.EstadoOrden;
import com.logistica.ms_workorders.model.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    List<OrdenTrabajo> findByEstado(EstadoOrden estado);
    List<OrdenTrabajo> findByBuildingId(Long buildingId);
    List<OrdenTrabajo> findByTecnicoId(Long tecnicoId);
    List<OrdenTrabajo> findByQuoteId(Long quoteId);
}