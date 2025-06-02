package com.proyecto.spikyhair.DTO;

import java.util.List;

import com.proyecto.spikyhair.enums.Estado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservasDto {

    private Long id;
    private String fecha;
    private Estado estado;
    private String duracion;
    private List<ServiciosDto> servicios;

}
