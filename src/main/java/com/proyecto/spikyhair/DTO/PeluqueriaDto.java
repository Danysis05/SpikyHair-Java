package com.proyecto.spikyhair.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.proyecto.spikyhair.entity.Peluqueria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeluqueriaDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String horarioApertura;
    private String horarioCierre;
    private String descripcion;
    private String imagenUrl;
    private MultipartFile archivoImagen;
    private Long usuarioId;

    public PeluqueriaDto(Peluqueria peluqueria) {
    if (peluqueria != null) {
        this.id = peluqueria.getId();
        this.nombre = peluqueria.getNombre();
        this.direccion = peluqueria.getDireccion();
        this.telefono = peluqueria.getTelefono();
        this.email = peluqueria.getEmail();
        this.horarioApertura = peluqueria.getHorarioApertura();
        this.horarioCierre = peluqueria.getHorarioCierre();
        this.descripcion = peluqueria.getDescripcion();
        this.imagenUrl = peluqueria.getImagenUrl();
        this.usuarioId = peluqueria.getUsuario() != null ? peluqueria.getUsuario().getId() : null;
    }
}

}

