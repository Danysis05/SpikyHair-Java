package com.proyecto.spikyhair.controller;

import com.proyecto.spikyhair.DTO.RolDto;
import com.proyecto.spikyhair.service.RolService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // GET /api/roles
    @GetMapping
    public ResponseEntity<List<RolDto>> getAllRoles() {
        return ResponseEntity.ok(rolService.getAll());
    }

    // GET /api/roles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RolDto> getRolById(@PathVariable Long id) {
        RolDto dto = rolService.getById(id);
        return ResponseEntity.ok(dto);
    }

    // POST /api/roles
    @PostMapping
    public ResponseEntity<RolDto> createRol(@RequestBody RolDto rolDto) {
        RolDto nuevoRol = rolService.save(rolDto);
        return ResponseEntity.ok(nuevoRol);
    }

    // PUT /api/roles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RolDto> updateRol(@PathVariable Long id, @RequestBody RolDto rolDto) {
        RolDto actualizado = rolService.update(id, rolDto);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /api/roles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
