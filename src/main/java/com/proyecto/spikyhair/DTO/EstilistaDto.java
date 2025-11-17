package com.proyecto.spikyhair.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstilistaDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private Long peluqueriaId;
    private PeluqueriaDto peluqueria;
    
    public PeluqueriaDto getPeluqueria() {
        return peluqueria;
    }

    public void setPeluqueria(PeluqueriaDto peluqueria) {
        this.peluqueria = peluqueria;
    }
}

