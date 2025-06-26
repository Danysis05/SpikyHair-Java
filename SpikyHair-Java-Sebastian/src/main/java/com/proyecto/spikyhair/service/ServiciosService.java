package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.repository.ServiciosRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class ServiciosService implements Idao<Servicios, Long> {

    @Autowired
    private ServiciosRepository serviciosRepository;
    @Override
    public List<Servicios> getAll() {
        return serviciosRepository.findAll();
    }

    @Override
    public Servicios getById(Long id) {
        Optional<Servicios> optionalServicio = serviciosRepository.findById(id);
        return optionalServicio.orElse(null); // O lanzar una excepción si no se encuentra
    }
    @Override
    public Servicios create(Servicios entity) {
        return serviciosRepository.save(entity);
    }

    @Override
    public Servicios update(Servicios entity) {
        if (serviciosRepository.existsById(entity.getId())) {
            return serviciosRepository.save(entity);
        }
        return null; // O lanzar una excepción si no existe
    }

    @Override
    public void delete(Long id) {
        if (serviciosRepository.existsById(id)) {
            serviciosRepository.deleteById(id);
        }
        // Si no existe, podrías lanzar una excepción si lo deseas
    }
}
