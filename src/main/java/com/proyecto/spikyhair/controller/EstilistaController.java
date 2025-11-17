package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.spikyhair.DTO.EstilistaDto;
import com.proyecto.spikyhair.service.EstilistaService;

@Controller
@RequestMapping("/estilistas")
public class EstilistaController {

    private final EstilistaService estilistaService;

    public EstilistaController(EstilistaService estilistaService) {
        this.estilistaService = estilistaService;
    }

    @GetMapping("/listar")
    public List<EstilistaDto> listarEstilistas() {
        
        return estilistaService.getAll();
    }
}