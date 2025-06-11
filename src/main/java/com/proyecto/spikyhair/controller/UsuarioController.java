package com.proyecto.spikyhair.controller;

import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Mostrar todos los usuarios
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.getAll();
    }
   
    // Mostrar formulario para nuevo usuario
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario"; // Vista: usuarios/formulario.html
    }

    // Guardar nuevo usuario

    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.create(usuario);
        return new ResponseEntity<>(creado, HttpStatus.CREATED); 
    }

 
    // Mostrar formulario para editar usuario
    @PutMapping("/editar/{id}")
    public ResponseEntity <Usuario> actualizarUsuario(@PathVariable Long id,@RequestBody Usuario datosNuevos) {
        Usuario usuarioExistente = usuarioService.getById(id);
        if (usuarioExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            if (datosNuevos.getNombre() != null) {
                usuarioExistente.setNombre(datosNuevos.getNombre());
            }
            if (datosNuevos.getEmail() != null) {
                usuarioExistente.setEmail(datosNuevos.getEmail());
            }
            if (datosNuevos.getContrasena() != null) {
                usuarioExistente.setContrasena(datosNuevos.getContrasena());
            }
            if (datosNuevos.getRol() != null) {
                usuarioExistente.setRol(datosNuevos.getRol());
            }

            usuarioService.update(usuarioExistente);
            return ResponseEntity.ok(usuarioExistente);
        }
    }

    // Eliminar usuario
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

}
