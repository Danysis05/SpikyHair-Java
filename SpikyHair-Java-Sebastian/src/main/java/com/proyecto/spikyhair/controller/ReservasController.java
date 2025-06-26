package com.proyecto.spikyhair.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ReservasController {

    private List<String> reservas = new ArrayList<>();

    @GetMapping("/reservas")
    public String mostrarReservas(Model model) {
        model.addAttribute("reservas", reservas);
        return "reservas";
    }

    @PostMapping("/reservar")
    public String hacerReserva(@RequestParam String servicio, Model model) {
        reservas.add(servicio);
        model.addAttribute("reservas", reservas);
        return "redirect:/reservas";
    }
}
