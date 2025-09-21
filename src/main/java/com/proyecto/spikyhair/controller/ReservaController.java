package com.proyecto.spikyhair.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.enums.Estado;
import com.proyecto.spikyhair.repository.ReservasRepository;
import com.proyecto.spikyhair.service.ReservasService;
import com.proyecto.spikyhair.service.ServiciosService;
import com.proyecto.spikyhair.service.UsuarioService;


@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservasService reservasService;
    private final UsuarioService usuarioService;
    private final ServiciosService serviciosService;
    private final ReservasRepository ReservasRepository;

    public ReservaController(ReservasService reservasService, UsuarioService usuarioService,
                             ServiciosService serviciosService, ReservasRepository reservasRepository) {
        this.reservasService = reservasService;
        this.usuarioService = usuarioService;
        this.serviciosService = serviciosService;
        this.ReservasRepository = reservasRepository;
    }

    // Mostrar todas las reservas
@GetMapping("/mostrar")
public String listReservas(Model model) {
    Usuario usuario = usuarioService.getUsuarioAutenticado();

    // Obtenemos reservas del usuario
    List<Reserva> reservasEntidad = ReservasRepository.findByUsuarioId(usuario.getId());

    // Convertimos cada Reserva a ReservasDto
    List<ReservasDto> reservas = reservasEntidad.stream()
        .map(ReservasDto::new)
        .collect(Collectors.toList());

    model.addAttribute("usuario", usuario);
    model.addAttribute("reservas", reservas);
    return "usuario/reservas";
}




    // Ver una reserva por ID
    @GetMapping("/{id}")
    public String viewReserva(@PathVariable Long id, Model model) {
        ReservasDto reserva = reservasService.getById(id);
        model.addAttribute("reserva", reserva);
        return "reservas/view"; // Vista: templates/reservas/view.html
    }

    // Formulario para nueva reserva
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reserva", new ReservasDto());
        return "reservas/form"; // Vista: templates/reservas/form.html
    }

    // Guardar nueva reserva
@PostMapping("/crear")
public String createReserva(@ModelAttribute("reserva") ReservasDto dto) {
    Usuario usuario = usuarioService.getUsuarioAutenticado();
    if (usuario == null) throw new RuntimeException("Usuario no autenticado");

    UsuarioDto usuarioDto = new UsuarioDto();
    usuarioDto.setId(usuario.getId());
    usuarioDto.setNombre(usuario.getNombre()); // si tienes más campos relevantes
    dto.setUsuario(usuarioDto);

    ServiciosDto servicio = serviciosService.getById(dto.getServicioId());
    dto.setServicio(servicio);
    dto.setDuracion(servicio.getDuracion());
    dto.setEstado(Estado.PENDIENTE.name());

    reservasService.save(dto);

    return "redirect:/reservas/mostrar"; // Redirige a la lista de reservas
}



    // Formulario para editar reserva
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        
        ReservasDto reserva = reservasService.getById(id);

        model.addAttribute("reserva", reserva);
        return "reservas/form"; // Reutiliza la misma vista para editar
    }

    // Actualizar reserva
@PostMapping("/editar")
public String editReserva(@ModelAttribute("reserva") ReservasDto dto) {
    if (dto.getId() == null) {
        throw new IllegalArgumentException("El ID de la reserva es requerido para editar");
    }

    reservasService.update(dto.getId(), dto);
    return "redirect:/reservas/mostrar";
}

    // Eliminar reserva
@PostMapping("/eliminar/{id}")
public String eliminarReserva(@PathVariable Long id, Authentication authentication) {
    reservasService.delete(id);

    if (authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
        return "redirect:/admin/dashboard";
    } else {
        return "redirect:/reservas/mostrar"; // Asegúrate de que esta ruta exista
    }
}

// En tu controlador (ej: DashboardController o ReservaController)
@PostMapping("/cambiar-estado/{id}")
@ResponseBody
public ResponseEntity<Map<String, Object>> cambiarEstado(@PathVariable Long id) {
    Map<String, Object> response = new HashMap<>();
    Reserva reserva = reservasService.getReservaById(id);

    if(reserva == null) {
        response.put("success", false);
        response.put("error", "Reserva no encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Alternar estado
    Estado nuevoEstado = (reserva.getEstado() == Estado.PENDIENTE)
            ? Estado.REALIZADA
            : Estado.PENDIENTE;
    reserva.setEstado(nuevoEstado);

    reservasService.saveReserva(reserva);

    response.put("success", true);
    response.put("estado", nuevoEstado.name());
    return ResponseEntity.ok(response);
}



    // Contar reservas por estado
    @GetMapping("/contar")
    @ResponseBody
    public Map<String, Long> contarReservasPorEstado() {
        long pendientes = reservasService.contarPendientes();
        long realizadas = reservasService.contarRealizadas();

        Map<String, Long> conteo = new HashMap<>();
        conteo.put("PENDIENTE", pendientes);
        conteo.put("REALIZADA", realizadas);
        return conteo;
    }







}
