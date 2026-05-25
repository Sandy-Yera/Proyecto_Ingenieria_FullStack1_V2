package com.logistica.ms_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistica.ms_auth.model.UserCredencial;

import java.util.Optional;

public interface UserCredencialRepository extends JpaRepository<UserCredencial, Long> {
    public boolean existsByUsername(String username);
    Optional<UserCredencial> findByUsername(String username);
}
