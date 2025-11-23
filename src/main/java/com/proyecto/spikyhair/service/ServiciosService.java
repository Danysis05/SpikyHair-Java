package com.proyecto.spikyhair.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.repository.PeluqueriaRepository;
import com.proyecto.spikyhair.repository.ServiciosRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class ServiciosService implements Idao<Servicios, Long, ServiciosDto> {

    private final ServiciosRepository serviciosRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private static final String RUTA_IMAGENES = System.getProperty("user.dir") + "/uploads/servicios/";
    private final PeluqueriaRepository peluqueriaRepository;



    public ServiciosService(ServiciosRepository serviciosRepository, PeluqueriaRepository peluqueriaRepository) {
        this.serviciosRepository = serviciosRepository;
        this.peluqueriaRepository = peluqueriaRepository;
    }

    private Servicios convertirDtoAEntidad(ServiciosDto dto) {
        Servicios servicio = new Servicios();
        servicio.setId(dto.getId());
        servicio.setNombre(dto.getNombre());
        servicio.setDuracion(dto.getDuracion());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setImagen(dto.getImagen());
        return servicio;
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

    public void guardarServicio(ServiciosDto dto, MultipartFile imagenFile) throws IOException {

    // --- 1. Convertir DTO a entidad ---
    Servicios servicio = new Servicios();
    servicio.setNombre(dto.getNombre());
    servicio.setDuracion(dto.getDuracion());
    servicio.setDescripcion(dto.getDescripcion());
    servicio.setPrecio(dto.getPrecio());

    // --- 2. Asignar peluquería usando el ID del DTO ---
    if (dto.getPeluqueria_id() != null) {
        Peluqueria peluqueria = peluqueriaRepository.findById(dto.getPeluqueria_id())
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        servicio.setPeluqueria(peluqueria);
    } else {
        throw new RuntimeException("El id de peluquería no puede ser null");
    }

    // --- 3. Manejar imagen ---
    if (imagenFile != null && !imagenFile.isEmpty()) {
        // Asegura que la carpeta exista
        Files.createDirectories(Paths.get(RUTA_IMAGENES));

        String nombreImagen = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
        Path rutaImagen = Paths.get(RUTA_IMAGENES).resolve(nombreImagen);

        Files.copy(imagenFile.getInputStream(), rutaImagen, StandardCopyOption.REPLACE_EXISTING);

        // Guarda la ruta relativa con carpeta
        servicio.setImagen("servicios/" + nombreImagen);
    }

    // --- 4. Guardar en la base de datos ---
    serviciosRepository.save(servicio);
}

    
    public ServiciosDto update(Long id, ServiciosDto dto, MultipartFile nuevaImagen) throws IOException {
    Servicios existente = serviciosRepository.findById(id).orElseThrow();

    existente.setNombre(dto.getNombre());
    existente.setPrecio(dto.getPrecio());
    existente.setDescripcion(dto.getDescripcion());
    existente.setDuracion(dto.getDuracion());

    if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
        String rutaRelativa = guardarImagen(nuevaImagen, "servicios");
        existente.setImagen(rutaRelativa);
    }

    Servicios actualizado = serviciosRepository.save(existente);
    return modelMapper.map(actualizado, ServiciosDto.class);
}

    @Override
    public void delete(Long id) {
        serviciosRepository.deleteById(id);
    }

    @Override
    public long count() {
        return serviciosRepository.count();
    }
private String guardarImagen(MultipartFile imagenFile, String subcarpeta) throws IOException {
    if (imagenFile == null || imagenFile.isEmpty()) return null;

    String carpeta = System.getProperty("user.dir") + "/uploads/" + subcarpeta + "/";
    Files.createDirectories(Paths.get(carpeta));

    String nombreArchivo = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
    Path rutaArchivo = Paths.get(carpeta).resolve(nombreArchivo);
    Files.copy(imagenFile.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

    return subcarpeta + "/" + nombreArchivo;
}
public List<ServiciosDto> filtrarServicios(String nombre, Double precioMin, Double precioMax) {
    return serviciosRepository.findAll()
        .stream()
        .filter(servicio -> {
            boolean coincideNombre = (nombre == null || nombre.isBlank()) ||
                    servicio.getNombre().toLowerCase().contains(nombre.toLowerCase());
            boolean coincidePrecioMin = (precioMin == null) || (servicio.getPrecio() >= precioMin);
            boolean coincidePrecioMax = (precioMax == null) || (servicio.getPrecio() <= precioMax);
            return coincideNombre && coincidePrecioMin && coincidePrecioMax;
        })
        .map(servicio -> modelMapper.map(servicio, ServiciosDto.class))
        .collect(Collectors.toList());
}




    @Override
    public ServiciosDto save(ServiciosDto dto) {
        throw new UnsupportedOperationException("Usa guardarServicio() en su lugar.");
    }

    @Override
    public ServiciosDto update(Long id, ServiciosDto dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<ServiciosDto> getByPeluqueriaId(Long peluqueriaId) {
        return serviciosRepository.findByPeluqueriaId(peluqueriaId)
                .stream()
                .map(servicio -> modelMapper.map(servicio, ServiciosDto.class))
                .collect(Collectors.toList());
    }
}
