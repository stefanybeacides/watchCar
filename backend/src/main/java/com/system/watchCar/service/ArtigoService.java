package com.system.watchCar.service;

import com.system.watchCar.entity.Artigo;
import com.system.watchCar.repository.ArtigoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtigoService {

    private final ArtigoRepository artigoRepository;

    public ArtigoService(ArtigoRepository artigoRepository) {
        this.artigoRepository = artigoRepository;
    }

    public List<Artigo> listarTodos() {
        return artigoRepository.findAll();
    }
}
