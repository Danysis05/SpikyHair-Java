package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proyecto.spikyhair.DTO.ResenasDto;
import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Resena;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.PeluqueriaRepository;
import com.proyecto.spikyhair.repository.ResenasRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ResenaService implements Idao<Resena, Long, ResenasDto> {

    private final ResenasRepository resenasRepository;
    private final ModelMapper modelMapper;
    private final PeluqueriaRepository peluqueriaRepository;
    private final UsuarioRepository usuarioRepository;
    

    public ResenaService(ResenasRepository resenasRepository,
         ModelMapper modelMapper, PeluqueriaRepository peluqueriaRepository,
        UsuarioRepository usuarioRepository) {
        this.resenasRepository = resenasRepository;
        this.modelMapper = modelMapper;
        this.peluqueriaRepository = peluqueriaRepository;
        this.usuarioRepository = usuarioRepository;

    }

@Override
public ResenasDto save(ResenasDto dto) {

    // 1. Crear entidad manualmente (evita errores de ModelMapper)
    Resena resena = new Resena();
    resena.setComentario(dto.getComentario());
    resena.setPuntuacion(dto.getPuntuacion());

    // 2. Asignar usuario
    Usuario usuario = usuarioRepository.findById(dto.getUsuario().getId())
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    resena.setUsuario(usuario);

    // 3. Asignar peluquería
    Peluqueria peluqueria = peluqueriaRepository.findById(dto.getPeluqueriaId())
            .orElseThrow(() -> new EntityNotFoundException("Peluquería no encontrada"));
    resena.setPeluqueria(peluqueria);

    // 4. Guardar reseña
    Resena saved = resenasRepository.save(resena);

    // 5. Convertir a DTO manualmente para mayor control
    ResenasDto response = new ResenasDto();
    response.setId(saved.getId());
    response.setComentario(saved.getComentario());
    response.setPuntuacion(saved.getPuntuacion());
    response.setPeluqueriaId(saved.getPeluqueria().getId());
    response.setUsuario(new UsuarioDto(saved.getUsuario()));

    return response;
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
        public Double obtenerPromedioPorPeluqueria(Long peluqueriaId) {
        Double promedio = resenasRepository.obtenerPromedioPorPeluqueria(peluqueriaId);
        return promedio != null ? promedio : 0.0;
    }

   public List<ResenasDto> buscarPorQuery(Long peluqueriaId, String q) {
    List<Resena> resenas;

    if (!StringUtils.hasText(q)) {
        resenas = resenasRepository.findByPeluqueriaId(peluqueriaId);
    } else {
        resenas = resenasRepository.buscarPorQuery(peluqueriaId, q.trim());
    }

    // Mapeo manual de Resena a ResenasDto
    return resenas.stream()
            .map(r -> new ResenasDto(
                    r.getId(),
                    r.getComentario(),
                    r.getPuntuacion(),
                    new UsuarioDto(r.getUsuario()), // asumimos que UsuarioDto tiene constructor que recibe Usuario
                    r.getPeluqueria().getId()
            ))
            .toList();
}

}





