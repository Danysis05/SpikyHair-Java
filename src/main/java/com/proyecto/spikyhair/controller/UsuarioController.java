package com.proyecto.spikyhair.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Peluqueria;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.PeluqueriaService;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PeluqueriaService peluqueriaService;
    public UsuarioController(UsuarioService usuarioService, PeluqueriaService peluqueriaService) {
        this.usuarioService = usuarioService;
        this.peluqueriaService = peluqueriaService;
    }

    @GetMapping("/home")
    public String paginaUsuario(Model model) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        model.addAttribute("usuario", usuario);
        return "usuario/home";
    }

    // Mostrar todos los usuarios



    // Mostrar un usuario por ID
    @GetMapping("/{id}")
    public String viewUsuario(@PathVariable Long id, Model model) {
        UsuarioDto usuario = usuarioService.getById(id);
        model.addAttribute("usuario", usuario);
        return "usuarios/view";
    }

    // Formulario para editar un usuario
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UsuarioDto usuario = usuarioService.getById(id);
        model.addAttribute("usuario", usuario);
        return "usuarios/form";
    }

 @PostMapping("/guardar")
public String guardarUsuario(@ModelAttribute("usuario") UsuarioDto usuarioDto,
                             @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
    try {
        String rutaCarpeta = System.getProperty("user.dir") + "/uploads/";
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) carpeta.mkdirs();

        // Procesar nueva imagen
        if (imagen != null && !imagen.isEmpty()) {
            String originalFilename = imagen.getOriginalFilename();
            String nombreOriginal = (originalFilename != null) ?
                    originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") :
                    "archivo_sin_nombre";

            String nombreArchivo = UUID.randomUUID() + "_" + nombreOriginal;
            imagen.transferTo(new File(rutaCarpeta + nombreArchivo));

            usuarioDto.setImagenPerfil(nombreArchivo);

            // Eliminar imagen anterior
            if (usuarioDto.getId() != null) {
                UsuarioDto existente = usuarioService.getById(usuarioDto.getId());
                if (existente.getImagenPerfil() != null && !existente.getImagenPerfil().isEmpty()) {
                    File anterior = new File(rutaCarpeta + existente.getImagenPerfil());
                    if (anterior.exists()) anterior.delete();
                }
            }
        } else if (usuarioDto.getId() != null) {
            // Mantener imagen anterior
            UsuarioDto existente = usuarioService.getById(usuarioDto.getId());
            usuarioDto.setImagenPerfil(existente.getImagenPerfil());
        }

        // Diferenciar creación vs actualización
        if (usuarioDto.getId() == null) {
            // Nuevo usuario → contraseña obligatoria
            if (usuarioDto.getContrasena() == null || usuarioDto.getContrasena().isEmpty()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para un nuevo usuario");
            }
            usuarioService.save(usuarioDto);
        } else {
            // Actualización → NO tocar contraseña si viene vacía
            UsuarioDto existente = usuarioService.getById(usuarioDto.getId());

            // ⛔ NO copiar la contraseña encriptada al DTO
            //   (esto causaba doble encriptación dentro del service)
            if (usuarioDto.getContrasena() == null || usuarioDto.getContrasena().isEmpty()) {
                usuarioDto.setContrasena(null);  // 🔥 importante
            }

            // El service ya debe verificar si contrasena == null para no encriptarla
            usuarioService.update(usuarioDto.getId(), usuarioDto);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    Usuario usuario = usuarioService.getUsuarioAutenticado();
    if (usuario != null) {
        String rol = usuario.getRol().getNombre();
        switch (rol) {
            case "ADMINISTRADOR": return "redirect:/admin/dashboard";
            case "DUEÑO": return "redirect:/owners/dashboard";
            default: return "redirect:/home/page";
        }
    }
    return "redirect:/login";
}



@PostMapping("/cambiar-rol")
@ResponseBody
public ResponseEntity<Map<String, String>> cambiarRol(@RequestBody Map<String, Long> payload) {

    Map<String, String> response = new HashMap<>();

    try {
        Long usuarioId = payload.get("usuarioId");
        Long rolId = payload.get("rolId");

        usuarioService.actualizarRol(usuarioId, rolId);

        response.put("success", "Rol actualizado correctamente");
        return ResponseEntity.ok(response);

    } catch (IllegalArgumentException e) {
        response.put("warning", e.getMessage());
        return ResponseEntity.badRequest().body(response);

    } catch (Exception e) {
        response.put("error", "Ocurrió un error inesperado al cambiar el rol.");
        return ResponseEntity.internalServerError().body(response);
    }
}



 @GetMapping("/delete/{id}")
public String deleteUsuario(@PathVariable Long id, RedirectAttributes redirectAttrs) {
    var usuarioAutenticado = usuarioService.getUsuarioAutenticado();

    if (usuarioAutenticado == null) {
        redirectAttrs.addFlashAttribute("error", "No autorizado");
        return "redirect:/usuarios";
    }

    // Eliminar peluquería asociada al usuario (si existe)
    Optional<Peluqueria> optionalPeluqueria = peluqueriaService.findOptionalByUsuarioId(id);
    optionalPeluqueria.ifPresent(peluqueria -> peluqueriaService.delete(peluqueria.getId()));

    // Eliminar usuario
    usuarioService.delete(id);

    // Si el usuario eliminó su propia cuenta
    if (usuarioAutenticado.getId().equals(id)) {
        redirectAttrs.addFlashAttribute("warning", "Usuario eliminado y sesión cerrada");
        return "redirect:/login";
    }

    // Si eliminó a otro usuario
    redirectAttrs.addFlashAttribute("success", "Usuario eliminado correctamente");
    return "redirect:/admin/dashboard";
}



    @GetMapping("/count")
    public String countUsuarios(Model model) {
        long totalUsuarios = usuarioService.countUsers();
        long totalAdmins = usuarioService.countAdmins();
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalAdmins", totalAdmins);
        return "usuarios/count"; // Asegúrate de tener una vista para mostrar estos datos
    }

}