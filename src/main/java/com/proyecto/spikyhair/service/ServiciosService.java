package com.proyecto.spikyhair.service;

import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.repository.ServiciosRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiciosService implements Idao<Servicios, Long, ServiciosDto> {

    private final ServiciosRepository serviciosRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public ServiciosService(ServiciosRepository serviciosRepository) {
        this.serviciosRepository = serviciosRepository;
    }

    @Override
    public List<ServiciosDto> getAll() {
        return serviciosRepository.findAll()
                .stream()
                .map(servicio -> modelMapper.map(servicio, ServiciosDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiciosDto getById(Long id) {
        Servicios servicio = serviciosRepository.findById(id).orElseThrow();
        return modelMapper.map(servicio, ServiciosDto.class);
    }

    @Override
    public ServiciosDto save(ServiciosDto dto) {
        Servicios servicio = modelMapper.map(dto, Servicios.class);
        Servicios creado = serviciosRepository.save(servicio);
        return modelMapper.map(creado, ServiciosDto.class);
    }

    @Override
    public ServiciosDto update(Long id, ServiciosDto dto) {
        Servicios existente = serviciosRepository.findById(id).orElseThrow();
        existente.setNombre(dto.getNombre());
        existente.setPrecio(dto.getPrecio());
        existente.setDescripcion(dto.getDescripcion());
        // agrega otros setters si hay más campos

        Servicios actualizado = serviciosRepository.save(existente);
        return modelMapper.map(actualizado, ServiciosDto.class);
    }

    @Override
    public void delete(Long id) {
        serviciosRepository.deleteById(id);
    }
}

