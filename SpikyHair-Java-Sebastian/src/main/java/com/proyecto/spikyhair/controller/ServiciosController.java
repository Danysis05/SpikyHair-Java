package com.proyecto.spikyhair.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiciosController {

    @GetMapping("/servicios")
    public String mostrarServicios() {
        return "servicios";
    }
}
