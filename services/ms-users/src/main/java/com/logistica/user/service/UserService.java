package com.logistica.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logistica.user.model.User;
import com.logistica.user.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> listaUsers() {
        return userRepository.findAll();
    }

    public Boolean existeUserId(Long id) {
        return userRepository.existsById(id);
    }

    public Boolean existeUserRut(Integer rut) {
        return userRepository.existsByRut(rut);
    }

    public User guardarUser(User user) {
        return userRepository.save(user);
    }
}
