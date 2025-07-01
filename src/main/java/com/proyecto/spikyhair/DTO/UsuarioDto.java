package com.proyecto.spikyhair.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioDto {
    private Long id;

    private String nombre;

    private String email;

    private String contrasena;

    private String telefono;

    private String imagenPerfil;


    private RolDto rol;


}
