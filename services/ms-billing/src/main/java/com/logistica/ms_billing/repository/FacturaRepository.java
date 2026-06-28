package com.logistica.ms_billing.repository;

import com.logistica.ms_billing.model.EstadoFactura;
import com.logistica.ms_billing.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByWorkOrderId(Long workOrderId);
    List<Factura> findByEstado(EstadoFactura estado);
    List<Factura> findByTecnicoId(Long tecnicoId);
    List<Factura> findByBuildingId(Long buildingId);
}