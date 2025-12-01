package com.proyecto.spikyhair.service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.entity.Estilista;
import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.entity.Servicios;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.enums.Estado;
import com.proyecto.spikyhair.repository.EstilistaRepository;
import com.proyecto.spikyhair.repository.ReservasRepository;
import com.proyecto.spikyhair.repository.ServiciosRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

import jakarta.mail.MessagingException;

@Service
public class ReservasService implements Idao<Reserva, Long, ReservasDto> {

    private final ReservasRepository reservasRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ServiciosRepository serviciosRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final EstilistaRepository estilistaRepository;

    public ReservasService(ReservasRepository reservasRepository,
                       ServiciosRepository serviciosRepository,
                       UsuarioRepository usuarioRepository,
                       EmailService emailService, ModelMapper modelMapper, EstilistaRepository estilistaRepository) {
    this.reservasRepository = reservasRepository;
    this.serviciosRepository = serviciosRepository;
    this.usuarioRepository = usuarioRepository;
    this.emailService = emailService;
    this.estilistaRepository = estilistaRepository;
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
    reserva.setHora(dto.getHora());
    reserva.setEstado(dto.getEstado() != null ? Estado.valueOf(dto.getEstado()) : Estado.PENDIENTE);

    Servicios servicio = serviciosRepository.findById(dto.getServicio().getId())
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    reserva.setServicios(servicio);

    // Peluquería obligatoria
    if (servicio.getPeluqueria() != null) {
        reserva.setPeluqueria(servicio.getPeluqueria());
    } else {
        throw new RuntimeException("Servicio sin peluquería asignada");
    }

    Usuario usuario = usuarioRepository.findById(dto.getUsuario().getId())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    reserva.setUsuario(usuario);

    // Estilista opcional
    if (dto.getEstilista() != null && dto.getEstilista().getId() != null) {
        Estilista estilista = estilistaRepository.findById(dto.getEstilista().getId())
            .orElseThrow(() -> new RuntimeException("Estilista no encontrado"));
        reserva.setEstilista(estilista);
    } else {
        reserva.setEstilista(null);
    }

    Reserva guardada = reservasRepository.save(reserva);

    try {
        emailService.enviarCorreoReserva(
            usuario.getEmail(),
            usuario.getNombre(),
            servicio.getNombre(),
            reserva.getFecha().toString(),
            reserva.getHora()
        );
    } catch (MessagingException e) {
        System.out.println("⚠ Error enviando correo: " + e.getMessage());
    }

    return new ReservasDto(guardada);
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

public long contarPendientes() {
    return reservasRepository.countByEstado(Estado.PENDIENTE);
}
public long contarRealizadas() {
    return reservasRepository.countByEstado(Estado.REALIZADA);
}

public Map<String, List<?>> obtenerEstadisticasServicios() {
    List<Object[]> resultados = reservasRepository.contarReservasPorServicio();

    List<String> labels = new ArrayList<>();
    List<Long> values = new ArrayList<>();

    for (Object[] fila : resultados) {
        labels.add((String) fila[0]);                // nombre del servicio
        values.add(((Number) fila[1]).longValue()); // cantidad de reservas
    }

    Map<String, List<?>> datos = new HashMap<>();
    datos.put("labels", labels);
    datos.put("values", values);

    return datos;
}
public List<String> obtenerMeses() {
        List<String> meses = new ArrayList<>();
        List<Object[]> resultados = reservasRepository.contarReservasPorMes();

        for (Object[] fila : resultados) {
            Integer mes = (Integer) fila[0];
            String nombreMes = Month.of(mes).getDisplayName(TextStyle.FULL, new Locale("es"));
            meses.add(nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1));
        }
        return meses;
    }

    public List<Long> obtenerReservasPorMes() {
        List<Long> valores = new ArrayList<>();
        List<Object[]> resultados = reservasRepository.contarReservasPorMes();

        for (Object[] fila : resultados) {
            valores.add((Long) fila[1]);
        }
        return valores;
    }

    public Double calcularIngresosTotales() {
        Double total = reservasRepository.obtenerIngresosTotales();
        return total != null ? total : 0.0;
    }
    public Map<String, Long> obtenerConteoPorServicio() {
    return reservasRepository.findAll().stream()
            .collect(Collectors.groupingBy(r -> r.getServicios().getNombre(), Collectors.counting()));
}
public List<Double> obtenerIngresosPorMes(int year) {
    List<Double> ingresosPorMes = new ArrayList<>(Collections.nCopies(12, 0.0));

    List<Reserva> reservas = reservasRepository.findAll();
    for (Reserva reserva : reservas) {
        if (reserva.getFecha() != null && reserva.getFecha().getYear() == year) {
            int mesIndex = reserva.getFecha().getMonthValue() - 1; // enero = 0
            if (reserva.getServicios() != null && reserva.getServicios().getPrecio() != null) {
                ingresosPorMes.set(mesIndex,
                        ingresosPorMes.get(mesIndex) + reserva.getServicios().getPrecio());
            }
        }
    }
    return ingresosPorMes;
}

    public Map<String, Long> obtenerClientesTop() {
        Map<String, Long> conteoClientes = reservasRepository.findAll().stream()
                .filter(r -> r.getUsuario() != null)
                .collect(Collectors.groupingBy(r -> r.getUsuario().getNombre(), Collectors.counting()));

        // Ordenar de mayor a menor
        return conteoClientes.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10) // opcional: mostrar solo top 10
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public List<ReservasDto> findByPeluqueriaId(Long peluqueriaId) {
    return reservasRepository.findByPeluqueriaId(peluqueriaId)
            .stream()
            .map(reserva -> new ReservasDto(reserva))
         
            .collect(Collectors.toList());
    }
public List<ReservasDto> buscarPorQuery(Long peluqueriaId, String q) {
    List<Reserva> reservas;

    // Si el texto de búsqueda es vacío o nulo, devolvemos todas las reservas de la peluquería
    if (!StringUtils.hasText(q)) {
        reservas = reservasRepository.findByPeluqueriaId(peluqueriaId);
    } else {
        // Usamos la query JPQL del repositorio para buscar por todos los atributos
        reservas = reservasRepository.buscarPorQuery(peluqueriaId, q.trim());
    }

    // Convertimos la lista de entidades a DTOs
    return reservas.stream()
            .map(r -> new ReservasDto(r))
            .toList();
}

public Map<String, List<?>> obtenerEstadisticasEstilistas(Long peluqueriaId) {
        List<Object[]> resultados = reservasRepository.contarReservasPorEstilista(peluqueriaId);

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (Object[] obj : resultados) {
            labels.add((String) obj[0]);
            values.add((Long) obj[1]);
        }

        Map<String, List<?>> estadisticas = new HashMap<>();
        estadisticas.put("labels", labels);
        estadisticas.put("values", values);

        return estadisticas;
    }

}




