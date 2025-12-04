package com.proyecto.spikyhair.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.PeluqueriaDto;
import com.proyecto.spikyhair.DTO.PeluqueriaTopDto;
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

    // Actualizar campos básicos
    existente.setNombre(dto.getNombre());
    existente.setDireccion(dto.getDireccion());
    existente.setTelefono(dto.getTelefono());
    existente.setEmail(dto.getEmail());
    existente.setDescripcion(dto.getDescripcion());

    // ⛔ EVITA SOBREESCRIBIR CON NULL
    if (dto.getHorarioApertura() != null) {
        existente.setHorarioApertura(dto.getHorarioApertura());
    }

    if (dto.getHorarioCierre() != null) {
        existente.setHorarioCierre(dto.getHorarioCierre());
    }

    // Imagen solo si llega algo
    if (dto.getImagenUrl() != null && !dto.getImagenUrl().isEmpty()) {
        existente.setImagenUrl(dto.getImagenUrl());
    }

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


public List<PeluqueriaDto> buscarPorQuery(String query) {
    if (query == null || query.trim().isEmpty()) {
        return peluqueriaRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, PeluqueriaDto.class))
                .toList();
    }

    return peluqueriaRepository.buscarPorQuery(query.trim())
            .stream()
            .map(p -> modelMapper.map(p, PeluqueriaDto.class))
            .toList();
}

public Optional<Peluqueria> findOptionalByUsuarioId(Long usuarioId) {
    return peluqueriaRepository.findByUsuarioId(usuarioId); // ya devuelve Optional
}

public List<PeluqueriaTopDto> obtenerTop5() {

    List<Object[]> filas = peluqueriaRepository.findTop5PeluqueriasWithPromedioRaw();
    List<PeluqueriaTopDto> lista = new ArrayList<>();

    for (Object[] f : filas) {

        PeluqueriaTopDto dto = new PeluqueriaTopDto(
            ((Number) f[0]).longValue(),            // id
            (String) f[1],                          // nombre
            (String) f[2],                          // direccion
            (String) f[3],                          // descripcion
            f[4] != null ? f[4].toString() : null,  // imagenUrl
            f[5] != null ? ((Number) f[5]).doubleValue() : 0.0 // promedio
        );

        lista.add(dto);
    }

    return lista;
}

public List<PeluqueriaTopDto> obtenerTop5PorReservas() {

    List<Object[]> filas = peluqueriaRepository.findTop5PeluqueriasPorReservas();
    List<PeluqueriaTopDto> lista = new ArrayList<>();

    for (Object[] f : filas) {

        PeluqueriaTopDto dto = new PeluqueriaTopDto(
            ((Number) f[0]).longValue(),            // id
            (String) f[1],                          // nombre
            (String) f[2],                          // direccion
            (String) f[3],                          // descripcion
            f[4] != null ? f[4].toString() : null,  // imagenUrl
            f[5] != null ? ((Number) f[5]).doubleValue() : 0.0 // totalReservas
        );

        lista.add(dto);
    }

    return lista;


}
}
