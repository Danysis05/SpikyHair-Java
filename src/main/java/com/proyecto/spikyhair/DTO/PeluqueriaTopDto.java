package com.proyecto.spikyhair.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeluqueriaTopDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String descripcion;
    private String imagenUrl;
    private Double promedio;
}
