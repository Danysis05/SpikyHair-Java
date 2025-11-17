package com.proyecto.spikyhair.DTO;

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
    private Long usuarioId;
}

