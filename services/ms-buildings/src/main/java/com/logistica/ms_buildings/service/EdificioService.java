package com.logistica.ms_buildings.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logistica.ms_buildings.model.Edificio;
import com.logistica.ms_buildings.repository.EdificioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EdificioService {
    private final EdificioRepository edificioRepository;

    public List<Edificio> listaEdificios() {
        return edificioRepository.findAll();
    }

    public Boolean existeEdificioId(Long id) {
        return edificioRepository.existsById(id);
    }

    public Edificio guardarEdificio(Edificio edificio) {
        return edificioRepository.save(edificio);
    }

}
