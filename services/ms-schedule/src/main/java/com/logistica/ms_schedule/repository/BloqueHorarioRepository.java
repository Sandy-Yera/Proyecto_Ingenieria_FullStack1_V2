package com.logistica.ms_schedule.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistica.ms_schedule.model.BloqueHorario;

@Repository
public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long> {

    List<BloqueHorario> findByTecnicoId(Long tecnicoId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM BloqueHorario b " +
           "WHERE b.tecnicoId = :tecnicoId AND b.inicio < :fin AND b.fin > :inicio")
    boolean existsSolapamiento(
            @Param("tecnicoId") Long tecnicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM BloqueHorario b " +
           "WHERE b.tecnicoId = :tecnicoId AND b.inicio < :fin AND b.fin > :inicio AND b.id <> :id")
    boolean existsSolapamientoExcluyendoId(
            @Param("tecnicoId") Long tecnicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("id") Long id);
}
