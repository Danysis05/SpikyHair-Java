package com.proyecto.spikyhair.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenasDto {
    private Long id;
    private String comentario;
    private int puntuacion;

    private Long usuarioId;
    private Long peluqueriaId;
}
