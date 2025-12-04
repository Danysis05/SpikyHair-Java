package com.proyecto.spikyhair.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.spikyhair.DTO.PeluqueriaDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/peluquerias") 
public class PeluqueriaController {

    private final PeluqueriaService peluqueriaService;
    private final UsuarioService usuarioService;
    public PeluqueriaController(PeluqueriaService peluqueriaService, UsuarioService usuarioService) {
        this.peluqueriaService = peluqueriaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public String listarPeluquerias(Model model) {
        List<PeluqueriaDto> peluquerias = peluqueriaService.getAll();
        model.addAttribute("peluquerias", peluquerias);
        return "Dashboard/peluquerias";
    }

    @PostMapping("/crear")
    public String crearPeluqueria(PeluqueriaDto peluqueriaDto) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        Long usuarioId = usuario.getId();
        peluqueriaDto.setUsuarioId(usuarioId);
        usuarioService.actualizarRol(usuarioId, 3L);
        peluqueriaService.save(peluqueriaDto);
        return "redirect:/owners/dashboard";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("peluqueria", new PeluqueriaDto());
        return "usuario/peluquriaRegister";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {         
        PeluqueriaDto peluqueriaDto = peluqueriaService.getById(id);
        model.addAttribute("peluqueria", peluqueriaDto);
        return "redirect:/owners/dashboard";
    }

@PostMapping("/actualizar/{id}")
public String actualizarPeluqueria(
        @PathVariable Long id,
        @ModelAttribute PeluqueriaDto peluqueriaDto,
        RedirectAttributes redirectAttributes) {

    try {
        // Obtener peluquería actual
        PeluqueriaDto peluqueria = peluqueriaService.getById(id);


        // -----------------------------
        //  SUBIDA DE IMAGEN
        // -----------------------------
        MultipartFile imagen = peluqueriaDto.getArchivoImagen();

        if (imagen != null && !imagen.isEmpty()) {

            String fileName = StringUtils.cleanPath(imagen.getOriginalFilename());
            String uploadDir = System.getProperty("user.dir") + "/uploads/peluquerias/";

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            Path path = Paths.get(uploadDir + fileName);
            Files.copy(imagen.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            peluqueriaDto.setImagenUrl(fileName); // solo el nombre
        } else {
            // Mantener imagen existente si no se envía nueva
            peluqueriaDto.setImagenUrl(peluqueria.getImagenUrl());
        }

        // Guardar cambios
        peluqueriaService.update(id, peluqueriaDto);

        // -----------------------------
        // MENSAJE DE ÉXITO
        // -----------------------------
        redirectAttributes.addFlashAttribute("success", "¡Datos actualizados correctamente!");

    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("error", "Error al subir la imagen.");
        e.printStackTrace();
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar la peluquería.");
    }

    return "redirect:/owners/dashboard";
}


}
