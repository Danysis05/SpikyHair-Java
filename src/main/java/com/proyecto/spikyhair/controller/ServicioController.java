package com.proyecto.spikyhair.controller;

import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.service.ServiciosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/servicios")
public class ServicioController {

    private final ServiciosService serviciosService;


    public ServicioController(ServiciosService serviciosService) {
        this.serviciosService = serviciosService;
    }

    @GetMapping
    public ResponseEntity<List<ServiciosDto>> listar() {
        return ResponseEntity.ok(serviciosService.getAll());
    }

    @PostMapping
    public ResponseEntity<ServiciosDto> crear(@RequestBody ServiciosDto serviciosDTO) {
        ServiciosDto creadoDTO = serviciosService.save(serviciosDTO );
        return new ResponseEntity<>(creadoDTO, HttpStatus.CREATED);
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<ServiciosDto> actualizarServicio(@PathVariable Long id, @RequestBody ServiciosDto datosNuevosDTO) {
        ServiciosDto existenteDTO = serviciosService.getById(id);
        if (existenteDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Solo actualiza campos si no son null
        if (datosNuevosDTO.getNombre() != null) existenteDTO.setNombre(datosNuevosDTO.getNombre());
        if (datosNuevosDTO.getDuracion() != null) existenteDTO.setDuracion(datosNuevosDTO.getDuracion());
        if (datosNuevosDTO.getDescripcion() != null) existenteDTO.setDescripcion(datosNuevosDTO.getDescripcion());
        if (datosNuevosDTO.getPrecio() != null) existenteDTO.setPrecio(datosNuevosDTO.getPrecio());
        if (datosNuevosDTO.getImagen() != null) existenteDTO.setImagen(datosNuevosDTO.getImagen());

        ServiciosDto actualizadoDTO = serviciosService.update(id, existenteDTO);
        return ResponseEntity.ok(actualizadoDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) {
        ServiciosDto servicioDto = serviciosService.getById(id);
        if (servicioDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        serviciosService.delete(id);
        return ResponseEntity.ok().build();
    }
}

