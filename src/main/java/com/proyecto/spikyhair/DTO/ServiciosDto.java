package com.proyecto.spikyhair.DTO;

import com.proyecto.spikyhair.entity.Servicios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ServiciosDto {
    private Long id;

    private String nombre;

    private String duracion;

    private String descripcion;

    private Double precio;

    private String imagen;

    private Long peluqueria_id;

    public ServiciosDto(Servicios servicio) {
    if (servicio != null) {
        this.id = servicio.getId();
        this.nombre = servicio.getNombre();
        this.duracion = servicio.getDuracion();
        this.descripcion = servicio.getDescripcion();
        this.precio = servicio.getPrecio();
        this.imagen = servicio.getImagen();
        this.peluqueria_id = servicio.getPeluqueria() != null ? servicio.getPeluqueria().getId() : null;
    }
}

}
