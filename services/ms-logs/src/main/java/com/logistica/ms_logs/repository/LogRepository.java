package com.logistica.ms_logs.repository;

import com.logistica.ms_logs.model.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 🔵 SOLUCIÓN MEDIO 3: Repositorio Optimizado con Paginación.
 * Permite realizar búsquedas inteligentes y segmentadas sobre la tabla de auditoría,
 * evitando la sobrecarga de memoria por consultas masivas.
 */
@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {

    /**
     * Busca logs aplicando filtros combinados por servicio y nivel con soporte de paginación.
     * Al usar 'Containing', permitimos búsquedas parciales e ignoramos si vienen vacíos.
     */
    Page<LogEntity> findByServiceNameContainingAndLevelContaining(String service, String level, Pageable pageable);
}