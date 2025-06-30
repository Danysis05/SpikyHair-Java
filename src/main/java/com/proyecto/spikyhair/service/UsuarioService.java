package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class UsuarioService implements Idao<Usuario, Long, UsuarioDto> {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
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

        // Asignar el rol "USUARIO" por defecto
        Rol rolUsuario = rolRepository.findByNombre("USUARIO")
            .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));
        usuario.setRol(rolUsuario);

        // Encriptar contraseña
        String hashed = passwordEncoder.encode(dto.getContrasena());
        usuario.setContrasena(hashed);

        Usuario creado = usuarioRepository.save(usuario);
        return modelMapper.map(creado, UsuarioDto.class);
    }

    @Override
    public UsuarioDto update(Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow();
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());

        // Solo encripta si se proporciona una nueva contraseña
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            String hashed = passwordEncoder.encode(dto.getContrasena());
            existente.setContrasena(hashed);
        }

        Usuario actualizado = usuarioRepository.save(existente);
        return modelMapper.map(actualizado, UsuarioDto.class);
    }


    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }


}
