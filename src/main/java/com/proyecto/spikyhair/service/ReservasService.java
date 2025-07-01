package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.ReservasRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class ReservasService implements Idao<Reserva, Long, ReservasDto> {

    private final ReservasRepository reservasRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public ReservasService(ReservasRepository reservasRepository) {
        this.reservasRepository = reservasRepository;
    }

    @Override
    public List<ReservasDto> getAll() {
        return reservasRepository.findAll()
                .stream()
                .map(reserva -> modelMapper.map(reserva, ReservasDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReservasDto getById(Long id) {
        Reserva reserva = reservasRepository.findById(id).orElseThrow();
        return modelMapper.map(reserva, ReservasDto.class);
    }

@Override
    public ReservasDto save(ReservasDto dto) {
        Reserva reserva = modelMapper.map(dto, Reserva.class);
        Reserva guardada = reservasRepository.save(reserva);
        return modelMapper.map(guardada, ReservasDto.class);
    }

    @Override
    public ReservasDto update(Long id, ReservasDto dto) {
        Reserva existente = reservasRepository.findById(id).orElseThrow();

        // Mapea campos simples
        existente.setFecha(dto.getFecha());

        // Convierte DTOs a entidades
        Usuario usuario = modelMapper.map(dto.getUsuario(), Usuario.class);
        existente.setUsuario(usuario);

        Servicios servicios = modelMapper.map(dto.getServicios(), Servicios.class);
        existente.setServicios(servicios);

        Reserva actualizada = reservasRepository.save(existente);
        return modelMapper.map(actualizada, ReservasDto.class);
    }

    @Override
    public void delete(Long id) {
        reservasRepository.deleteById(id);
    }
    @Override
    public long count() {
        return reservasRepository.count();
    }

}
