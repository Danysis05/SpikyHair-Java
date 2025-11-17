package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.ResenasDto;
import com.proyecto.spikyhair.entity.Resena;
import com.proyecto.spikyhair.repository.ResenasRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class ResenaService implements Idao<Resena, Long, ResenasDto> {

    private final ResenasRepository resenasRepository;
    private final ModelMapper modelMapper;

    public ResenaService(ResenasRepository resenasRepository, ModelMapper modelMapper) {
        this.resenasRepository = resenasRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResenasDto save(ResenasDto dto) {
        Resena resena = modelMapper.map(dto, Resena.class);
        Resena saved = resenasRepository.save(resena);
        return modelMapper.map(saved, ResenasDto.class);
    }

    @Override
    public ResenasDto update(Long id, ResenasDto dto) {
        Resena existente = resenasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La reseña con ID " + id + " no existe."));

        existente.setComentario(dto.getComentario());
        existente.setPuntuacion(dto.getPuntuacion()); // ✔ CORRECTO

        Resena actualizado = resenasRepository.save(existente);
        return modelMapper.map(actualizado, ResenasDto.class);
    }

    @Override
    public List<ResenasDto> getAll() {
        return resenasRepository.findAll()
                .stream()
                .map(resena -> modelMapper.map(resena, ResenasDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ResenasDto getById(Long id) {   // ✔ FALTABA ESTE MÉTODO
        Resena resena = resenasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La reseña con ID " + id + " no existe."));
        return modelMapper.map(resena, ResenasDto.class);
    }

    @Override
    public void delete(Long id) {
        resenasRepository.deleteById(id);
    }

    @Override
    public long count() {
        return resenasRepository.count();
    }

    public List<ResenasDto> getByPeluqueriaId(Long peluqueriaId) {
        return resenasRepository.findByPeluqueriaId(peluqueriaId)
                .stream()
                .map(resena -> modelMapper.map(resena, ResenasDto.class))
                .collect(Collectors.toList());
    }


}
