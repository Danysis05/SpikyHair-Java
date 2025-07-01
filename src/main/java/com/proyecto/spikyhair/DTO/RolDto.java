package com.proyecto.spikyhair.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDto {

    private Long id;

    private String nombre;

    @ToString.Exclude
    private List<UsuarioDto> usuarios;
}
