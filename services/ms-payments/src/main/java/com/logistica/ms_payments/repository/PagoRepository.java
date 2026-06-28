package com.logistica.ms_payments.repository;

import com.logistica.ms_payments.model.EstadoPago;
import com.logistica.ms_payments.model.MetodoPago;
import com.logistica.ms_payments.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFacturaId(Long facturaId);
    List<Pago> findByEstado(EstadoPago estado);
    List<Pago> findByMetodoPago(MetodoPago metodo);
}