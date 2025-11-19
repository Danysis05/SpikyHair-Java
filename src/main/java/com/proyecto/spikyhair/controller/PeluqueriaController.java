package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.PeluqueriaDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/peluquerias") 
public class PeluqueriaController {

    private final PeluqueriaService peluqueriaService;
    private final UsuarioService usuarioService;
    public PeluqueriaController(PeluqueriaService peluqueriaService, UsuarioService usuarioService) {
        this.peluqueriaService = peluqueriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public String listarPeluquerias(Model model) {
        List<PeluqueriaDto> peluquerias = peluqueriaService.getAll();
        model.addAttribute("peluquerias", peluquerias);
        return "Dashboard/peluquerias";
    }

    @PostMapping("/crear")
    public String crearPeluqueria(PeluqueriaDto peluqueriaDto) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        Long usuarioId = usuario.getId();
        peluqueriaDto.setUsuarioId(usuarioId);
        usuarioService.actualizarRol(usuarioId, 3L);
        peluqueriaService.save(peluqueriaDto);
        return "redirect:/owners/dashboard";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("peluqueria", new PeluqueriaDto());
        return "Dashboard/crear_peluqueria";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {         
        PeluqueriaDto peluqueriaDto = peluqueriaService.getById(id);
        model.addAttribute("peluqueria", peluqueriaDto);
        return "Dashboard/editar_peluqueria";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarPeluqueria(@PathVariable Long id, PeluqueriaDto peluqueriaDto) {
        peluqueriaDto.setId(id);
        peluqueriaService.update(id, peluqueriaDto);
        return "redirect:/peluquerias/lista";
    }

}
