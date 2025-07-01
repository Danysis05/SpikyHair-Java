package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.ReservasService;
import com.proyecto.spikyhair.service.ServiciosService;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UsuarioService usuarioService;
    private final ServiciosService serviciosService;
    private final ReservasService reservasService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository RolRepository;

    public AdminController(UsuarioService usuarioService, ServiciosService serviciosService, ReservasService reservasService, UsuarioRepository usuarioRepository, RolRepository RolRepository) {
        this.usuarioService = usuarioService;
        this.serviciosService = serviciosService;
        this.reservasService = reservasService;
        this.usuarioRepository = usuarioRepository;
        this.RolRepository = RolRepository;
    }
@GetMapping("/dashboard")
public String dashboardAdmin(Model model) {
    Usuario usuario = usuarioService.getUsuarioAutenticado();
    List<UsuarioDto> usuarios = usuarioService.getAll();
    List<ServiciosDto> servicios = serviciosService.getAll();
    List<ReservasDto> reservas = reservasService.getAll();
    long totalUsuarios = usuarioRepository.count();
    long totalServicios = serviciosService.count();
    long totalReservas = reservasService.count();
    model.addAttribute("roles", RolRepository.findAll());

    model.addAttribute("usuario", usuario);
    model.addAttribute("usuarios", usuarios);
    model.addAttribute("servicios", servicios);
    model.addAttribute("reservas", reservas);
    model.addAttribute("totalUsuarios", totalUsuarios);
    model.addAttribute("totalServicios", totalServicios);
    model.addAttribute("totalReservas", totalReservas);
    model.addAttribute("servicio", new ServiciosDto());

    return "admin/dashboard"; // NO redirección
}



}
