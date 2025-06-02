package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.repository.ReservasRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class ReservasService implements Idao<Reserva, Long> {

    @Autowired

    private ReservasRepository reservasRepository;
    @Override
    public List<Reserva> getAll() {
        return reservasRepository.findAll();
    }
    
    @Override
    public Reserva getById(Long id) {
         Optional<Reserva> optionalReserva = reservasRepository.findById(id);
         return optionalReserva.orElse(null); // O lanzar excepción personalizada
    }

    @Override
    public Reserva create(Reserva entity) {
        return reservasRepository.save(entity);
    }

    @Override
    public Reserva update(Reserva entity) {
        if (reservasRepository.existsById(entity.getId())) {
            return reservasRepository.save(entity);
        }
        return null; // O lanzar excepción si no existe
    }

    @Override
    public void delete(Long id) {
        if (reservasRepository.existsById(id)) {
            reservasRepository.deleteById(id);
        }
    }
}
