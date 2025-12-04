package com.proyecto.spikyhair.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proyecto.spikyhair.entity.Reserva;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservasDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private String estado;

    private String duracion;

    private String hora;

    private Long servicioId; 
    private ServiciosDto servicio;  

    private EstilistaDto estilista;
    private Long peluqueriaId;


    private PeluqueriaDto peluqueria;

    private UsuarioDto usuario;

    private String servicioNombre;

    // Constructor para convertir de entidad Reserva a DTO
    public ReservasDto(Reserva reserva) {
        if (reserva != null) {
            this.id = reserva.getId();
            this.fecha = reserva.getFecha();
            this.hora = reserva.getHora();
            this.duracion = reserva.getDuracion();
            this.estado = reserva.getEstado() != null ? reserva.getEstado().toString() : null;

            // Mapeo de Peluqueria
            if (reserva.getPeluqueria() != null) {
                this.peluqueria = new PeluqueriaDto();
                this.peluqueria.setId(reserva.getPeluqueria().getId());
                this.peluqueria.setNombre(reserva.getPeluqueria().getNombre());
                this.peluqueria.setDireccion(reserva.getPeluqueria().getDireccion());
                this.peluqueria.setTelefono(reserva.getPeluqueria().getTelefono());
                this.peluqueria.setEmail(reserva.getPeluqueria().getEmail());
                this.peluqueria.setHorarioApertura(reserva.getPeluqueria().getHorarioApertura());
                this.peluqueria.setHorarioCierre(reserva.getPeluqueria().getHorarioCierre());
                this.peluqueria.setDescripcion(reserva.getPeluqueria().getDescripcion());
                this.peluqueria.setImagenUrl(reserva.getPeluqueria().getImagenUrl());
                this.peluqueria.setUsuarioId(reserva.getPeluqueria().getUsuario() != null ? reserva.getPeluqueria().getUsuario().getId() : null);
            }

            // Mapeo de Servicio
            if (reserva.getServicios() != null) {
                this.servicio = new ServiciosDto();
                this.servicio.setId(reserva.getServicios().getId());
                this.servicio.setNombre(reserva.getServicios().getNombre());
                this.servicio.setDuracion(reserva.getServicios().getDuracion());
                this.servicio.setDescripcion(reserva.getServicios().getDescripcion());
                this.servicio.setPrecio(reserva.getServicios().getPrecio());
                this.servicio.setImagen(reserva.getServicios().getImagen());
                this.servicio.setPeluqueria_id(reserva.getServicios().getPeluqueria() != null ? reserva.getServicios().getPeluqueria().getId() : null);
            }

            // Mapeo de Estilista
            if (reserva.getEstilista() != null) {
                this.estilista = new EstilistaDto();
                this.estilista.setId(reserva.getEstilista().getId());
                this.estilista.setNombre(reserva.getEstilista().getNombre());
                this.estilista.setEspecialidad(reserva.getEstilista().getEspecialidad());
                this.estilista.setPeluqueriaId(reserva.getEstilista().getPeluqueria() != null ? reserva.getEstilista().getPeluqueria().getId() : null);
                this.estilista.setImagenPerfil(reserva.getEstilista().getImagenPerfil());
            }

            // Mapeo de Usuario
            if (reserva.getUsuario() != null) {
                this.usuario = new UsuarioDto(reserva.getUsuario());
            }

            // Nombre del servicio para mostrar en listas
            this.servicioNombre = reserva.getServicios() != null ? reserva.getServicios().getNombre() : null;

            // IDs para uso en formularios si se requiere
            this.servicioId = reserva.getServicios() != null ? reserva.getServicios().getId() : null;
        }
    }
}
