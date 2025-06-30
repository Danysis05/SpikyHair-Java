package com.proyecto.spikyhair.controller;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.service.ReservasService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservasService reservasService;

    public ReservaController(ReservasService reservasService) {
        this.reservasService = reservasService;
    }

    // Mostrar todas las reservas
    @GetMapping
    public String listReservas(Model model) {
        List<ReservasDto> reservas = reservasService.getAll();
        model.addAttribute("reservas", reservas);
        return "reservas/list"; // Vista: templates/reservas/list.html
    }

    // Ver una reserva por ID
    @GetMapping("/{id}")
    public String viewReserva(@PathVariable Long id, Model model) {
        ReservasDto reserva = reservasService.getById(id);
        model.addAttribute("reserva", reserva);
        return "reservas/view"; // Vista: templates/reservas/view.html
    }

    // Formulario para nueva reserva
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reserva", new ReservasDto());
        return "reservas/form"; // Vista: templates/reservas/form.html
    }

    // Guardar nueva reserva
    @PostMapping
    public String createReserva(@ModelAttribute("reserva") ReservasDto dto) {
        reservasService.save(dto);
        return "redirect:/reservas";
    }

    // Formulario para editar reserva
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ReservasDto reserva = reservasService.getById(id);
        model.addAttribute("reserva", reserva);
        return "reservas/form"; // Reutiliza la misma vista para editar
    }

    // Actualizar reserva
    @PostMapping("/update/{id}")
    public String updateReserva(@PathVariable Long id, @ModelAttribute("reserva") ReservasDto dto) {
        reservasService.update(id, dto);
        return "redirect:/reservas";
    }

    // Eliminar reserva
    @GetMapping("/delete/{id}")
    public String deleteReserva(@PathVariable Long id) {
        reservasService.delete(id);
        return "redirect:/reservas";
    }
}
