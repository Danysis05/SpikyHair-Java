package com.proyecto.spikyhair.DTO;

import com.proyecto.spikyhair.entity.Reserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservasDto {

    private Long id;
    private String fecha;
    private String estado;
    private String duracion;
    private Long servicioId; 
    private ServiciosDto servicio;  

    private UsuarioDto usuario;
    private String servicioNombre;


public ReservasDto(Reserva reserva) {
    this.id = reserva.getId();
    this.fecha = reserva.getFecha();
    this.duracion = reserva.getDuracion();
    this.estado = reserva.getEstado().toString();
    this.usuario = new UsuarioDto(reserva.getUsuario()); // Asegúrate de tener este constructor
    this.servicioNombre = reserva.getServicios().getNombre();
}



}
