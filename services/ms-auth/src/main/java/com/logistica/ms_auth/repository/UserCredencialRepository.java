package com.logistica.ms_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistica.ms_auth.model.UserCredencial;

public interface UserCredencialRepository extends JpaRepository<UserCredencial, Long>{

}
