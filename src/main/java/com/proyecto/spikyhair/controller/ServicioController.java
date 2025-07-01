package com.proyecto.spikyhair.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.service.ServiciosService;

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
    @PostMapping("/create")
    public String crearServicio(@ModelAttribute ServiciosDto servicioDto,
                                 @RequestParam("imagenFile") MultipartFile imagenFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            serviciosService.guardarServicio(servicioDto, imagenFile);
            redirectAttributes.addFlashAttribute("success", "Servicio guardado con éxito.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el servicio.");
        }
        return "redirect:/admin/dashboard";
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
public String actualizarServicio(@PathVariable Long id,
                                 @ModelAttribute("servicio") ServiciosDto datosNuevosDTO,
                                 @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                 RedirectAttributes redirectAttributes) {
    try {
        serviciosService.update(id, datosNuevosDTO, imagenFile);
        redirectAttributes.addFlashAttribute("success", "Servicio actualizado con éxito.");
    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar la imagen del servicio.");
    }
    return "redirect:/admin/dashboard?seccion=servicios";
}




    // Eliminar servicio
    @GetMapping("/delete/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        ServiciosDto servicioDto = serviciosService.getById(id);
        if (servicioDto != null) {
            serviciosService.delete(id);
        }
        return "redirect:/admin/dashboard?seccion=servicios";
    }
}
