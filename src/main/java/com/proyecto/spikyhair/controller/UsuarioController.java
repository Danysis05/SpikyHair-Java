package com.proyecto.spikyhair.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;
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

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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

    // Guardar o actualizar usuario (unificado)
@PostMapping("/guardar")
public String guardarUsuario(@ModelAttribute("usuario") UsuarioDto usuarioDto,
                             @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
    try {
        String rutaCarpeta = System.getProperty("user.dir") + "/uploads/";
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) carpeta.mkdirs();

        if (imagen != null && !imagen.isEmpty()) {
            String originalFilename = imagen.getOriginalFilename();
            String nombreOriginal = (originalFilename != null) ? originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") : "archivo_sin_nombre";
            String nombreArchivo = UUID.randomUUID() + "_" + nombreOriginal;
            String ruta = rutaCarpeta + nombreArchivo;

            imagen.transferTo(new File(ruta));
            usuarioDto.setImagenPerfil(nombreArchivo); // SOLO EL NOMBRE

            // Eliminar imagen anterior si existe
            if (usuarioDto.getId() != null) {
                UsuarioDto existente = usuarioService.getById(usuarioDto.getId());
                if (existente.getImagenPerfil() != null && !existente.getImagenPerfil().isEmpty()) {
                    File anterior = new File(rutaCarpeta + existente.getImagenPerfil());
                    if (anterior.exists()) anterior.delete();
                }
            }

        } else if (usuarioDto.getId() != null) {
            UsuarioDto existente = usuarioService.getById(usuarioDto.getId());
            usuarioDto.setImagenPerfil(existente.getImagenPerfil());
        }

        if (usuarioDto.getId() == null) {
            usuarioService.save(usuarioDto);
        } else {
            usuarioService.update(usuarioDto.getId(), usuarioDto);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    var usuario = usuarioService.getUsuarioAutenticado();

    if (usuario != null && usuario.getRol().getNombre().equals("ADMINISTRADOR")) {
        return "redirect:/admin/dashboard";
    } else if(usuario != null && usuario.getRol().getNombre().equals("DUEÑO")){
        return "redirect:/owners/dashboard";
    }
    else {
        return "redirect:/usuarios/home";
    }
}

@PostMapping("/cambiar-rol")
@ResponseBody
public ResponseEntity<String> cambiarRol(@RequestBody Map<String, Long> payload) {
    Long usuarioId = payload.get("usuarioId");
    Long rolId = payload.get("rolId");
    usuarioService.actualizarRol(usuarioId, rolId);
    return ResponseEntity.ok("Rol actualizado correctamente");
}


    // Eliminar usuario
    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id) {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado();

        usuarioService.delete(id);

        // Si el usuario autenticado eliminó su propia cuenta, debe cerrar sesión (redirigir al login)
        if (usuarioAutenticado != null && usuarioAutenticado.getId().equals(id)) {
            return ResponseEntity.status(205).body("Eliminado propio"); // 205 Reset Content (o usa otro status personalizado)
        }

        return ResponseEntity.ok("Usuario eliminado");
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
