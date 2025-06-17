package com.proyecto.spikyhair.service;

import com.proyecto.spikyhair.DTO.RolDto;
import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolService implements Idao<Rol, Long, RolDto> {

    private final RolRepository rolRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public List<RolDto> getAll() {
        return rolRepository.findAll()
                .stream()
                .map(rol -> modelMapper.map(rol, RolDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RolDto getById(Long id) {
        Rol rol = rolRepository.findById(id).orElseThrow();
        return modelMapper.map(rol, RolDto.class);
    }

    @Override
    public RolDto save(RolDto dto) {
        Rol rol = modelMapper.map(dto, Rol.class);
        Rol saved = rolRepository.save(rol);
        return modelMapper.map(saved, RolDto.class);
    }

    @Override
    public RolDto update(Long id, RolDto dto) {
        Rol existente = rolRepository.findById(id).orElseThrow();
        existente.setNombre(dto.getNombre());
        Rol actualizado = rolRepository.save(existente);
        return modelMapper.map(actualizado, RolDto.class);
    }

    @Override
    public void delete(Long id) {
        rolRepository.deleteById(id);
    }
}
