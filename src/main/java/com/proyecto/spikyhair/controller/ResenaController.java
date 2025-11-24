package com.proyecto.spikyhair.controller;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.ResenasDto;
import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.ResenaService;
import com.proyecto.spikyhair.service.UsuarioService;

import jakarta.validation.Valid;



@Controller
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService reseñaService;
    private final UsuarioService usuarioService;

    public ResenaController(ResenaService reseñaService, UsuarioService usuarioService) {
        this.reseñaService = reseñaService;
        this.usuarioService = usuarioService;
        
    }

@PostMapping("/crear/{peluqueriaId}")
public String crearResena(
        @PathVariable Long peluqueriaId,
        @Valid ResenasDto resenaDto,
        BindingResult result) {

    // Validar datos del formulario
    if (result.hasErrors()) {
        return "redirect:/peluquerias/perfil/" + peluqueriaId + "?error=datosInvalidos";
    }

    try {
        // 1. Obtener usuario autenticado
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        UsuarioDto usuarioDto = new UsuarioDto(usuario);

        // 2. Completar DTO
        resenaDto.setUsuario(usuarioDto);
        resenaDto.setPeluqueriaId(peluqueriaId);

        // 3. Guardar
        reseñaService.save(resenaDto);

    } catch (Exception e) {
        return "redirect:/peluquerias/perfil/" + peluqueriaId + "?error=" + e.getMessage();
    }

    // 4. Redirigir al perfil
    return "redirect:/peluquerias/perfil/" + peluqueriaId;
}

}
