package com.logistica.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.logistica.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // CORREGIDO: Cambiado de Integer a String para soportar el RUT completo bruto
    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);

    // CORREGIDO: Cambiado de Integer a String para la búsqueda por RUT completo
    Optional<User> findByRut(String rut);
}