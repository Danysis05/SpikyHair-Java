package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.service.RolService;

@Controller
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // Mostrar la lista de roles
    @GetMapping
    public String listarRoles(Model model) {
        List<Rol> roles = rolService.getAll();
        model.addAttribute("roles", roles);
        return "roles/lista"; // Vista HTML: src/main/resources/templates/roles/lista.html
    }

    // Mostrar formulario para nuevo rol
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("rol", new Rol());
        return "roles/formulario"; // Vista HTML: roles/formulario.html
    }

    // Guardar nuevo rol
    @PostMapping("/guardar")
    public String guardarRol(@ModelAttribute Rol rol) {
        rolService.create(rol);
        return "redirect:/roles";
    }

    // Mostrar formulario para editar rol existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Rol rol = rolService.getById(id);
        if (rol != null) {
            model.addAttribute("rol", rol);
            return "roles/formulario"; // Reutiliza el mismo formulario
        } else {
            return "redirect:/roles"; // O muestra un error
        }
    }

    // Eliminar un rol por ID
    @GetMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable Long id) {
        rolService.delete(id);
        return "redirect:/roles";
    }
}
