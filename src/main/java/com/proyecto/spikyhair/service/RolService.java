package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class RolService implements Idao<Rol, Long> {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> getAll() {
        return rolRepository.findAll();
    }

    @Override
    public Rol getById(Long id) {
        Optional<Rol> optionalRol = rolRepository.findById(id);
        return optionalRol.orElse(null);
    }

    @Override
    public Rol create(Rol entity) {
        return rolRepository.save(entity);
    }

    @Override
    public Rol update(Rol entity) {
        if (rolRepository.existsById(entity.getId())) {
            return rolRepository.save(entity);
        }
        return null; 
    }

    @Override
    public void delete(Long id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
        }
        
    }
}
