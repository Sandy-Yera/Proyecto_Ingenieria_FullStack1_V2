package com.logistica.ms_buildings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistica.ms_buildings.model.Edificio;

/**
 * REPOSITORIO JPA — Edificio
 * Hereda operaciones CRUD completas de JpaRepository (save, findById, findAll, deleteById, etc.).
 * Las consultas personalizadas se declaran aquí como métodos derivados de Spring Data JPA.
 */
public interface EdificioRepository extends JpaRepository<Edificio, Long> {

    /**
     * Verifica si existe un edificio registrado con el RUT de administrador indicado.
     * Usado en la capa de servicio para prevenir duplicados de administrador.
     */
    boolean existsByRutAdministrador(String rutAdministrador);

    /**
     * Verifica duplicado de RUT excluyendo el propio registro en actualizaciones.
     */
    boolean existsByRutAdministradorAndIdNot(String rutAdministrador, Long id);
}
