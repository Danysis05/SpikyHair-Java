package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proyecto.spikyhair.DTO.EstilistaDto;
import com.proyecto.spikyhair.entity.Estilista;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.repository.EstilistaRepository;
import com.proyecto.spikyhair.repository.PeluqueriaRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class EstilistaService implements Idao<Estilista, Long, EstilistaDto> {

    private final EstilistaRepository estilistaRepository;
    private final ModelMapper modelMapper;
    private final PeluqueriaRepository peluqueriaRepository;


    public EstilistaService(EstilistaRepository estilistaRepository, ModelMapper modelMapper, PeluqueriaRepository peluqueriaRepository) {
        this.estilistaRepository = estilistaRepository;
        this.modelMapper = modelMapper;
        this.peluqueriaRepository = peluqueriaRepository;
    }

@Override
public EstilistaDto save(EstilistaDto dto) {
    // Mapear DTO a entidad usando ModelMapper
    Estilista estilista = modelMapper.map(dto, Estilista.class);

    // Guardar solo el nombre de la imagen
    if (dto.getImagenPerfil() != null && !dto.getImagenPerfil().isEmpty()) {
        String fileName = StringUtils.cleanPath(dto.getImagenPerfil());
        estilista.setImagenPerfil(fileName);
    }

    // Guardar entidad
    Estilista saved = estilistaRepository.save(estilista);

    // Devolver DTO actualizado (si quieres, puedes mapear también la imagenNombre si lo agregaste al DTO)
    return modelMapper.map(saved, EstilistaDto.class);
}

@Override
public EstilistaDto update(Long id, EstilistaDto dto) {
    Estilista existente = estilistaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El estilista con ID " + id + " no existe."));

    existente.setNombre(dto.getNombre());
    existente.setEspecialidad(dto.getEspecialidad());

    // Si el DTO incluye un ID de peluquería, actualizar la relación
    if (dto.getPeluqueriaId() != null) {
        Peluqueria peluqueria = peluqueriaRepository.findById(dto.getPeluqueriaId())
                .orElseThrow(() -> new RuntimeException("La peluquería con ID " + dto.getPeluqueriaId() + " no existe."));
        existente.setPeluqueria(peluqueria);
    }

    Estilista actualizado = estilistaRepository.save(existente);

    return modelMapper.map(actualizado, EstilistaDto.class);
}


    @Override
    public List<EstilistaDto> getAll() {
        return estilistaRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EstilistaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EstilistaDto getById(Long id) {
        Estilista estilista = estilistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El estilista con ID " + id + " no existe."));
        return modelMapper.map(estilista, EstilistaDto.class);
    }

    @Override
    public void delete(Long id) {
        estilistaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return estilistaRepository.count();
    }

    public List<EstilistaDto> getByPeluqueriaId(Long peluqueriaId) {
        return estilistaRepository.findByPeluqueriaId(peluqueriaId)
                .stream()
                .map(estilista -> modelMapper.map(estilista, EstilistaDto.class))
                .collect(Collectors.toList());
    }

public EstilistaDto saveOrUpdate(EstilistaDto dto) {

    Estilista estilista;

    // Si viene ID → actualizar
    if (dto.getId() != null) {

        estilista = estilistaRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException(
                        "El estilista con ID " + dto.getId() + " no existe."
                ));

        estilista.setNombre(dto.getNombre());
        estilista.setEspecialidad(dto.getEspecialidad());

    } else {
        estilista = modelMapper.map(dto, Estilista.class);
    }

    // ✔ CORRECCIÓN FINAL: usar imagenPerfil, ya NO imagenNombre
    if (dto.getImagenPerfil() != null && !dto.getImagenPerfil().isEmpty()) {
        estilista.setImagenPerfil(dto.getImagenPerfil());
    }

    // Actualizar peluquería si viene en el DTO
    if (dto.getPeluqueriaId() != null) {
        Peluqueria peluqueria = peluqueriaRepository.findById(dto.getPeluqueriaId())
                .orElseThrow(() -> new RuntimeException(
                        "La peluquería con ID " + dto.getPeluqueriaId() + " no existe."
                ));
        estilista.setPeluqueria(peluqueria);
    }

    Estilista saved = estilistaRepository.save(estilista);

    return modelMapper.map(saved, EstilistaDto.class);
}
public List<EstilistaDto> buscarPorQuery(Long peluqueriaId, String q) {
    List<Estilista> estilistas;

    if (!StringUtils.hasText(q)) {
        estilistas = estilistaRepository.findByPeluqueriaId(peluqueriaId);
    } else {
        estilistas = estilistaRepository.buscarPorQuery(peluqueriaId, q.trim());
    }

    // Mapear entidades a DTO
    return estilistas.stream()
            .map(e -> new EstilistaDto(
                    e.getId(),                    // id
                    e.getNombre(),                // nombre
                    e.getEspecialidad(),          // especialidad
                    e.getPeluqueria().getId(),    // peluqueriaId
                    null,                         // archivoImagen (si aplica)
                    e.getImagenPerfil()           // imagenPerfil
            ))
            .toList();
}

}






