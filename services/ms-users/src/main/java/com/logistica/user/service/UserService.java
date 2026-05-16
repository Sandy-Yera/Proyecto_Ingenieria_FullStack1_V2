package com.logistica.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logistica.user.model.User;
import com.logistica.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor //Comentario Temporal: RequieredArgsConstructor
public class UserService {
    // Eliminamos la notacion "@Autowired" al utilizar 
    // RequiredArgsConstructor de lombok ya no será necesario
    private final UserRepository userRepository;

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
