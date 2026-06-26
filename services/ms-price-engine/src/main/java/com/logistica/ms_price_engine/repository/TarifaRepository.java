package com.logistica.ms_price_engine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistica.ms_price_engine.model.Categoria;
import com.logistica.ms_price_engine.model.Tarifa;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    Optional<Tarifa> findByCategoria(Categoria categoria);
}
