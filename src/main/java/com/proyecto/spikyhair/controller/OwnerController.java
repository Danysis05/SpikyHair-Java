package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.EstilistaDto;
import com.proyecto.spikyhair.DTO.ResenasDto;
import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.EstilistaService;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.ResenaService;
import com.proyecto.spikyhair.service.ReservasService;
import com.proyecto.spikyhair.service.ServiciosService;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/owners")
public class OwnerController {
    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;
    private final EstilistaService estilistaService;
    private final ReservasService reservasService;
    private final ResenaService resenaService;
    private final ServiciosService serviciosService;

    public OwnerController(PeluqueriaService peluqueriaService, EstilistaService estilistaService, ReservasService reservasService, ResenaService resenaService, UsuarioService usuarioService, ServiciosService serviciosService) {
        this.peluqueriaService = peluqueriaService;
        this.estilistaService = estilistaService;
        this.reservasService = reservasService;
        this.resenaService = resenaService;
        this.usuarioService = usuarioService;
        this.serviciosService = serviciosService;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        long id_usuario = usuario.getId();
        Peluqueria peluqueria = peluqueriaService.findByUsuarioId(id_usuario);
        Long peluqueriaId = peluqueria.getId();
        List<EstilistaDto> estilistas = estilistaService.getByPeluqueriaId(peluqueriaId);
        List<ServiciosDto> servicios = serviciosService.getByPeluqueriaId(peluqueriaId);
        List<ReservasDto> reservas = reservasService.findByPeluqueriaId(peluqueriaId);
        List<ResenasDto> resenas = resenaService.getByPeluqueriaId(peluqueriaId);
        model.addAttribute("peluqueria", peluqueria);
        model.addAttribute("estilistas", estilistas);
        model.addAttribute("servicios", servicios);
        model.addAttribute("reservas", reservas);
        model.addAttribute("resenas", resenas);
        return "owner/dashboard"; 
    }
}