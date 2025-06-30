package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
     @GetMapping("/index")
    public String paginaUsuario() {
        return "usuario/index";
    }

    // Mostrar todos los usuarios
    @GetMapping
    public String listUsuarios(Model model) {
        List<UsuarioDto> usuarios = usuarioService.getAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/list"; // Vista HTML: usuarios/list.html
    }

    // Mostrar un usuario por ID
    @GetMapping("/{id}")
    public String viewUsuario(@PathVariable Long id, Model model) {
        UsuarioDto usuario = usuarioService.getById(id);
        model.addAttribute("usuario", usuario);
        return "usuarios/view"; // Vista HTML: usuarios/view.html
    }



    // Formulario para editar un usuario
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UsuarioDto usuario = usuarioService.getById(id);
        model.addAttribute("usuario", usuario);
        return "usuarios/form"; // Reutiliza el mismo formulario
    }

    // Actualizar usuario
    @PostMapping("/update/{id}")
    public String updateUsuario(@PathVariable Long id, @ModelAttribute("usuario") UsuarioDto usuarioDto) {
        usuarioService.update(id, usuarioDto);
        return "redirect:/usuarios";
    }

    // Eliminar usuario
    @GetMapping("/delete/{id}")
    public String deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return "redirect:/usuarios";
    }
}
