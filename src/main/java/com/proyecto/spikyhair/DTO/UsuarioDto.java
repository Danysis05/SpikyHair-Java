package com.proyecto.spikyhair.DTO;

import com.proyecto.spikyhair.entity.Usuario;

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

   public UsuarioDto(Usuario usuario) {
    this.id = usuario.getId();
    this.nombre = usuario.getNombre();
    this.email = usuario.getEmail();
    this.telefono = usuario.getTelefono();
    this.imagenPerfil = usuario.getImagenPerfil(); // <-- FALTABA ESTO

    if (usuario.getRol() != null) {
        this.rol = new RolDto(usuario.getRol());
    }
}

}



