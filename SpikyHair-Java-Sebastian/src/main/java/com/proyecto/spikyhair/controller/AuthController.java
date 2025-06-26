package com.proyecto.spikyhair.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        // Simulación de validación de usuario jaja
        if (email.equals("test@example.com") && password.equals("123456")) {
            // Guardamos el email 
            session.setAttribute("userEmail", email);
            // Redirigimos a la página principal
            return "redirect:/";
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }
    }

    @GetMapping("/register")
    public String mostrarRegistro() {
        return "register";
    }

    @PostMapping("/register")
    public String procesarRegistro(
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String repeatPassword,
            HttpSession session,
            Model model) {
        if (!password.equals(repeatPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }
        // Se guarda el email tras registrarse
        session.setAttribute("userEmail", email);
        // redirigimos a la página principal y melo
        return "redirect:/";
    }

}
