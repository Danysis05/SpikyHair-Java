package com.proyecto.spikyhair.controller;

import com.proyecto.spikyhair.DTO.RolDto;
import com.proyecto.spikyhair.service.RolService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // Mostrar todos los roles
    @GetMapping
    public String listRoles(Model model) {
        List<RolDto> roles = rolService.getAll();
        model.addAttribute("roles", roles);
        return "roles/list"; // Vista HTML: roles/list.html
    }

    // Mostrar un rol por ID
    @GetMapping("/{id}")
    public String viewRol(@PathVariable Long id, Model model) {
        RolDto rol = rolService.getById(id);
        model.addAttribute("rol", rol);
        return "roles/view"; // Vista HTML: roles/view.html
    }

    // Formulario para crear nuevo rol
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("rol", new RolDto());
        return "roles/form"; // Vista HTML: roles/form.html
    }

    // Guardar nuevo rol
    @PostMapping
    public String createRol(@ModelAttribute("rol") RolDto rolDto) {
        rolService.save(rolDto);
        return "redirect:/roles";
    }

    // Formulario para editar un rol
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        RolDto rol = rolService.getById(id);
        model.addAttribute("rol", rol);
        return "roles/form"; // Reutiliza el mismo formulario
    }

    // Actualizar rol
    @PostMapping("/update/{id}")
    public String updateRol(@PathVariable Long id, @ModelAttribute("rol") RolDto rolDto) {
        rolService.update(id, rolDto);
        return "redirect:/roles";
    }

    // Eliminar rol
    @GetMapping("/delete/{id}")
    public String deleteRol(@PathVariable Long id) {
        rolService.delete(id);
        return "redirect:/roles";
    }
}
