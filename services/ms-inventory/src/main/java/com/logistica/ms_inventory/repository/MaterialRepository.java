package com.logistica.ms_inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistica.ms_inventory.model.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    boolean existsByNombre(String nombre);
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
