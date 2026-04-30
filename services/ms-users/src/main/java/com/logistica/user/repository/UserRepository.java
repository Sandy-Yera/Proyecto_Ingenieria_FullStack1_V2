package com.logistica.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistica.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByRut(Integer rut);
}
