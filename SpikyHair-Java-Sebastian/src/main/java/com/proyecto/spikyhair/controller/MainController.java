package com.proyecto.spikyhair.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Muestra la página principal
    @GetMapping("/")
    public String mostrarHome(HttpSession session, Model model) {
        // Recupera el email guardado pa tin
        String email = (String) session.getAttribute("userEmail");
        // Si no hay sesión entonce redirige a login
        if (email == null) {
            return "redirect:/login";
        }
        // Añade el email al modelo para personalizar el saludo de bienvenida pa romper
        model.addAttribute("userEmail", email);
        return "main";
    }

    // Cierra la sesión y redirige a login
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
