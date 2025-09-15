package com.proyecto.spikyhair.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.enums.Estado;
import com.proyecto.spikyhair.repository.ReservasRepository;
import com.proyecto.spikyhair.repository.ServiciosRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

@Service
public class ReservasService implements Idao<Reserva, Long, ReservasDto> {

    private final ReservasRepository reservasRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ServiciosRepository serviciosRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservasService(ReservasRepository reservasRepository,
                           ServiciosRepository serviciosRepository,
                           UsuarioRepository usuarioRepository) {
        this.reservasRepository = reservasRepository;
        this.serviciosRepository = serviciosRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<ReservasDto> getAll() {
        List<Reserva> reservas = reservasRepository.findAll();
        return reservas.stream()
            .map(Reserva -> new ReservasDto(Reserva)) // Usa el constructor personalizado
            .collect(Collectors.toList());
    }

    @Override
    public ReservasDto getById(Long id) {
        Reserva reserva = reservasRepository.findById(id).orElseThrow();
        return new ReservasDto(reserva); // CORREGIDO: Usar constructor para incluir servicioNombre
    }

    @Override
    public ReservasDto save(ReservasDto dto) {
        if (dto.getServicio() == null || dto.getServicio().getId() == null) {
            throw new RuntimeException("El ID del servicio es requerido");
        }

        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new RuntimeException("El ID del usuario es requerido");
        }

        Reserva reserva = new Reserva();

        reserva.setFecha(dto.getFecha());
        reserva.setDuracion(dto.getDuracion());

        // Estado por defecto si no viene del formulario
        reserva.setEstado(dto.getEstado() != null ? Estado.valueOf(dto.getEstado()) : Estado.PENDIENTE);

        Servicios servicio = serviciosRepository.findById(dto.getServicio().getId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        reserva.setServicios(servicio);

        Usuario usuario = usuarioRepository.findById(dto.getUsuario().getId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        reserva.setUsuario(usuario);

        Reserva guardada = reservasRepository.save(reserva);
        return new ReservasDto(guardada); // CORREGIDO
    }

    @Override

public ReservasDto update(Long id, ReservasDto dto) {
    Reserva existente = reservasRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No existe la reserva con id " + id));

    // Solo actualizar lo que el usuario puede modificar
    if (dto.getFecha() != null) {
        existente.setFecha(dto.getFecha());
    }

    // No tocar usuario ni servicio, porque no los mandas desde el formulario

    Reserva actualizada = reservasRepository.save(existente);
    return new ReservasDto(actualizada);
}

    public List<ReservasDto> filtrarReservas(String nombreUsuario, String nombreServicio, String estado) {
    return reservasRepository.findAll().stream()
        .filter(r -> nombreUsuario == null || r.getUsuario().getNombre().toLowerCase().contains(nombreUsuario.toLowerCase()))
        .filter(r -> nombreServicio == null || r.getServicios().getNombre().toLowerCase().contains(nombreServicio.toLowerCase()))
        .filter(r -> estado == null || r.getEstado().name().equalsIgnoreCase(estado))
        .map(r -> new ReservasDto(r)) // corregido: usar constructor
        .collect(Collectors.toList());
}


    @Override
    public void delete(Long id) {
        reservasRepository.deleteById(id);
    }

    @Override
    public long count() {
        return reservasRepository.count();
    }
    public Reserva getReservaById(Long id) {
    return reservasRepository.findById(id).orElse(null);
}

public void saveReserva(Reserva reserva) {
    reservasRepository.save(reserva);
}
public List<String> obtenerMeses() {
    return List.of("Enero","Febrero","Marzo","Abril","Mayo","Junio",
                   "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
}

public List<Long> obtenerReservasPorMes() {
    // Aquí deberías consultar el repositorio y agrupar por mes
    return List.of(10L, 20L, 15L, 8L, 30L, 25L, 12L, 18L, 22L, 14L, 9L, 5L);
}
public long contarPendientes() {
    return reservasRepository.countByEstado(Estado.PENDIENTE);
}
public long contarRealizadas() {
    return reservasRepository.countByEstado(Estado.REALIZADA);
}



}
