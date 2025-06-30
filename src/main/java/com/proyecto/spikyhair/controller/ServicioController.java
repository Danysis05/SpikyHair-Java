package com.proyecto.spikyhair.controller;

import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.service.ServiciosService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    private final ServiciosService serviciosService;

    public ServicioController(ServiciosService serviciosService) {
        this.serviciosService = serviciosService;
    }

    // Mostrar todos los servicios
    @GetMapping
    public String listarServicios(Model model) {
        List<ServiciosDto> servicios = serviciosService.getAll();
        model.addAttribute("servicios", servicios);
        return "servicios/list"; // Vista: templates/servicios/list.html
    }

    // Ver un servicio por ID
    @GetMapping("/{id}")
    public String verServicio(@PathVariable Long id, Model model) {
        ServiciosDto servicio = serviciosService.getById(id);
        model.addAttribute("servicio", servicio);
        return "servicios/view"; // Vista: templates/servicios/view.html
    }

    // Mostrar formulario de creación
    @GetMapping("/new")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("servicio", new ServiciosDto());
        return "servicios/form"; // Vista: templates/servicios/form.html
    }

    // Guardar nuevo servicio
    @PostMapping
    public String crearServicio(@ModelAttribute("servicio") ServiciosDto serviciosDTO) {
        serviciosService.save(serviciosDTO);
        return "redirect:/servicios";
    }

    // Mostrar formulario de edición
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        ServiciosDto servicio = serviciosService.getById(id);
        if (servicio == null) {
            return "redirect:/servicios";
        }
        model.addAttribute("servicio", servicio);
        return "servicios/form"; // Reutiliza el mismo formulario
    }

    // Actualizar servicio
    @PostMapping("/update/{id}")
    public String actualizarServicio(@PathVariable Long id, @ModelAttribute("servicio") ServiciosDto datosNuevosDTO) {
        ServiciosDto existenteDTO = serviciosService.getById(id);
        if (existenteDTO == null) {
            return "redirect:/servicios";
        }

        // Solo actualiza campos si no son null
        if (datosNuevosDTO.getNombre() != null) existenteDTO.setNombre(datosNuevosDTO.getNombre());
        if (datosNuevosDTO.getDuracion() != null) existenteDTO.setDuracion(datosNuevosDTO.getDuracion());
        if (datosNuevosDTO.getDescripcion() != null) existenteDTO.setDescripcion(datosNuevosDTO.getDescripcion());
        if (datosNuevosDTO.getPrecio() != null) existenteDTO.setPrecio(datosNuevosDTO.getPrecio());
        if (datosNuevosDTO.getImagen() != null) existenteDTO.setImagen(datosNuevosDTO.getImagen());

        serviciosService.update(id, existenteDTO);
        return "redirect:/servicios";
    }

    // Eliminar servicio
    @GetMapping("/delete/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        ServiciosDto servicioDto = serviciosService.getById(id);
        if (servicioDto != null) {
            serviciosService.delete(id);
        }
        return "redirect:/servicios";
    }
}
