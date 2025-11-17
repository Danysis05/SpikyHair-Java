package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.PeluqueriaDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.repository.PeluqueriaRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class PeluqueriaService implements Idao<Peluqueria, Long, PeluqueriaDto> {

    private final PeluqueriaRepository peluqueriaRepository;
    private final ModelMapper modelMapper;

    public PeluqueriaService(PeluqueriaRepository peluqueriaRepository, ModelMapper modelMapper) {
        this.peluqueriaRepository = peluqueriaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PeluqueriaDto> getAll() {
        return peluqueriaRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, PeluqueriaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PeluqueriaDto getById(Long id) {
        Peluqueria peluqueria = peluqueriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La peluquería con ID " + id + " no existe"));
        return modelMapper.map(peluqueria, PeluqueriaDto.class);
    }

    @Override
    public PeluqueriaDto save(PeluqueriaDto dto) {
        Peluqueria peluqueria = modelMapper.map(dto, Peluqueria.class);
        Peluqueria guardada = peluqueriaRepository.save(peluqueria);
        return modelMapper.map(guardada, PeluqueriaDto.class);
    }

    @Override
    public PeluqueriaDto update(Long id, PeluqueriaDto dto) {
        Peluqueria existente = peluqueriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La peluquería con ID " + id + " no existe"));

        // Actualizar campos manualmente
        existente.setNombre(dto.getNombre());
        existente.setDireccion(dto.getDireccion());
        existente.setTelefono(dto.getTelefono());
        existente.setEmail(dto.getEmail());
        existente.setHorarioApertura(dto.getHorarioApertura());
        existente.setHorarioCierre(dto.getHorarioCierre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setImagenUrl(dto.getImagenUrl());

        Peluqueria actualizado = peluqueriaRepository.save(existente);
        return modelMapper.map(actualizado, PeluqueriaDto.class);
    }

    @Override
    public void delete(Long id) {
        peluqueriaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return peluqueriaRepository.count();
    }

    public Peluqueria findByUsuarioId(Long usuarioId) {
        return peluqueriaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró una peluquería para el usuario con ID " + usuarioId));
    }

}
