package com.proyecto.spikyhair.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDto {

    private Long id;

    private String nombre;

    private List<UsuarioDto> usuarios;

}
