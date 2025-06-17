package com.proyecto.spikyhair.service;

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements Idao<Usuario, Long, UsuarioDto> {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<UsuarioDto> getAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto getById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        return modelMapper.map(usuario, UsuarioDto.class);
    }

    @Override
    public UsuarioDto save(UsuarioDto dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        Usuario creado = usuarioRepository.save(usuario);
        return modelMapper.map(creado, UsuarioDto.class);
    }

    @Override
    public UsuarioDto update(Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow();
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setContrasena(dto.getContrasena());
        // Aquí puedes añadir más campos si los tiene UsuarioDto
        Usuario actualizado = usuarioRepository.save(existente);
        return modelMapper.map(actualizado, UsuarioDto.class);
    }

    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
