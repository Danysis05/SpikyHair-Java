package com.proyecto.spikyhair.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.spikyhair.DTO.EstilistaDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.CSVEstilistasService;
import com.proyecto.spikyhair.service.EstilistaService;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/estilistas")
public class EstilistaController {

    private final EstilistaService estilistaService;
    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;
    private final CSVEstilistasService csvEstilistasService;

    public EstilistaController(
            EstilistaService estilistaService,
            UsuarioService usuarioService,
            PeluqueriaService peluqueriaService,
            CSVEstilistasService csvEstilistasService
    ) {
        this.estilistaService = estilistaService;
        this.usuarioService = usuarioService;
        this.peluqueriaService = peluqueriaService;
        this.csvEstilistasService = csvEstilistasService;
    }

    @GetMapping("/listar")
    public List<EstilistaDto> listarEstilistas() {
        return estilistaService.getAll();
    }

    // ✔ Obtener estilista por ID para el modal de edición
    @GetMapping("/get/{id}")
    @ResponseBody
    public EstilistaDto obtenerEstilista(@PathVariable Long id) {
        return estilistaService.getById(id);
    }

    // ✔ Crear o actualizar estilista
    @PostMapping("/guardar")
    public String guardarEstilista(@ModelAttribute EstilistaDto estilistaDto) {
        try {
            // Usuario autenticado
            Usuario usuario = usuarioService.getUsuarioAutenticado();
            Peluqueria peluqueria = peluqueriaService.findByUsuarioId(usuario.getId());
            estilistaDto.setPeluqueriaId(peluqueria.getId());

            // ============================
            //  SUBIDA DE IMAGEN
            // ============================
            MultipartFile imagen = estilistaDto.getArchivoImagen();

            if (imagen != null && !imagen.isEmpty()) {

                String fileName = StringUtils.cleanPath(imagen.getOriginalFilename());
                String uploadDir = System.getProperty("user.dir") + "/uploads/estilistas/";

                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                Path path = Paths.get(uploadDir + fileName);
                Files.copy(imagen.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Guardamos SOLO el nombre del archivo en el DTO
                estilistaDto.setImagenPerfil(fileName);
            }

            estilistaService.saveOrUpdate(estilistaDto);

            return "redirect:/owners/dashboard";

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

@GetMapping("/delete/{id}")
public String eliminarEstilista(@PathVariable Long id, RedirectAttributes redirectAttributes) {

    try {
        EstilistaDto estilistaDto = estilistaService.getById(id);

        if (estilistaDto != null) {
            estilistaService.delete(id);
            redirectAttributes.addFlashAttribute("success", "¡El estilista fue eliminado correctamente!");
        } else {
            redirectAttributes.addFlashAttribute("warning", "El estilista que intentas eliminar no existe.");
        }

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el estilista.");
    }

    return "redirect:/owners/dashboard";
}


@PostMapping("/importar-csv")
public String importarCsvEstilistas(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

    try {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un archivo CSV.");
            return "redirect:/owners/dashboard";
        }

        // Importar desde el CSV
        int cantidad = csvEstilistasService.cargarEstilistasDesdeCsv(file);

        // Si todo salió bien, envía mensaje de éxito
        redirectAttributes.addFlashAttribute("success", "Se importaron " + cantidad + " estilistas.");
        return "redirect:/owners/dashboard";

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/owners/dashboard";
    }
}


}
