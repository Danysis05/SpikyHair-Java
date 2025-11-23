package com.proyecto.spikyhair.DTO;

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
}
