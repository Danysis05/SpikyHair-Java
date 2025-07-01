package com.proyecto.spikyhair.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.service.UsuarioService;


@Controller
@RequestMapping("/auth")
public class LoginController {
    private final UsuarioService usuarioService;
    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/logueo")
    public String defaultAfterLogin(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_ADMINISTRADOR" -> "redirect:/admin/dashboard";
            case "ROLE_USUARIO" -> "redirect:/usuario/index";
            default -> "redirect:/auth/login?error";
        };
    }

    @GetMapping("/new")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioDto());
        return "register"; // Retorna la vista de registro
    }
    @PostMapping("/register")
    public String registerUser(UsuarioDto usuarioDto) {
    
        usuarioService.save(usuarioDto);
        return "redirect:/auth/login"; // Redirigir a la página de login después de registrar
    }

    

 
  
}
