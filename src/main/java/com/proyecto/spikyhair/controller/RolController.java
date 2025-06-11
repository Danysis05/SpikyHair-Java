package com.proyecto.spikyhair.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.service.RolService;



@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // Mostrar la lista de roles
    @GetMapping
    public List<Rol> listarRoles() {
        return rolService.getAll(); // Esto se convierte automáticamente a JSON
    }

    // Guardar nuevo rol
    @PostMapping("/guardar")
    public ResponseEntity<Rol> guardarRol(@RequestBody Rol rol) {
        Rol creado = rolService.create(rol);
        return new ResponseEntity<>(creado,HttpStatus.CREATED);
    }

    // Mostrar formulario para editar rol existente

    @PutMapping("/update/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rolActualizado) {
        Rol rolExistente = rolService.getById(id);
        if (rolExistente != null) {
            rolActualizado.setId(id); // asegurar que se use el ID correcto
            Rol rolGuardado = rolService.update(rolActualizado);
            return ResponseEntity.ok(rolGuardado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

}
