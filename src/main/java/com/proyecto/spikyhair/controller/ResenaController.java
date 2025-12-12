package com.proyecto.spikyhair.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    if (result.hasErrors()) {
        return "redirect:/home/perfilPeluqueria/" + peluqueriaId + "?error=datosInvalidos";
    }

    try {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login?error=Debe iniciar sesión para comentar";
        }

        UsuarioDto usuarioDto = new UsuarioDto(usuario);

        resenaDto.setUsuario(usuarioDto);
        resenaDto.setPeluqueriaId(peluqueriaId);

        reseñaService.save(resenaDto);

    } catch (Exception e) {
        return "redirect:/home/perfilPeluqueria/" + peluqueriaId + "?error=" + e.getMessage();
    }

    return "redirect:/home/perfilPeluqueria/" + peluqueriaId + "?success=resenaCreada";

    
}
@PostMapping("eliminar/{id}")
@ResponseBody
public ResponseEntity<?> eliminarResena(@PathVariable Long id) {
    try {
        reseñaService.delete(id);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}


@PostMapping("/editar/{id}")
public String editarResena(@PathVariable Long id, @Valid ResenasDto resenaDto, BindingResult result) {
    if (result.hasErrors()) {
        return "redirect:/home/perfilPeluqueria/" + resenaDto.getPeluqueriaId() + "?error=datosInvalidos";
    }
    reseñaService.update(id, resenaDto);
    return "redirect:/home/perfilPeluqueria/" + resenaDto.getPeluqueriaId();
}



}
