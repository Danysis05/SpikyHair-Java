package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.EstilistaDto;
import com.proyecto.spikyhair.DTO.PeluqueriaDto;
import com.proyecto.spikyhair.DTO.PeluqueriaTopDto;
import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.EstilistaService;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.ServiciosService;
import com.proyecto.spikyhair.service.UsuarioService;



@Controller
@RequestMapping("home")
public class HomeController {

    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;
    private final ServiciosService serviciosService;
    private final EstilistaService estilistaService;

    public HomeController(UsuarioService usuarioService, PeluqueriaService peluqueriaService,
         ServiciosService serviciosService, EstilistaService estilistaService){
        this.usuarioService = usuarioService;
        this.peluqueriaService = peluqueriaService;
        this.serviciosService = serviciosService;
        this.estilistaService = estilistaService;

    }
    @GetMapping("page")
    public String page(Model model){
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        List<PeluqueriaTopDto> peluqueriasTop = peluqueriaService.obtenerTop5();
        model.addAttribute("peluqueriasTop", peluqueriasTop);
        model.addAttribute("usuario", usuario);
        return "usuario/home";

    }
    @GetMapping("perfilPeluqueria/{id}")
    public String peluqueria(@PathVariable Long id, Model model){
        PeluqueriaDto peluqueria = peluqueriaService.getById(id);
        List<ServiciosDto> servicios = serviciosService.getByPeluqueriaId(id);
        List<EstilistaDto> estilistas = estilistaService.getByPeluqueriaId(id);
        model.addAttribute("peluqueria", peluqueria);
        model.addAttribute("servicios", servicios);
        model.addAttribute("estilistas",estilistas);
        if (peluqueria == null) {
    return "redirect:/home/page"; // o mostrar error personalizado
}

        return "usuario/perfilPeluqueria";
    }

}
