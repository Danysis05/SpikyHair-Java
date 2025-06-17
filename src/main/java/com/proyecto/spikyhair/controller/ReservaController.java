package com.proyecto.spikyhair.controller;

import com.proyecto.spikyhair.DTO.ReservasDto;
import com.proyecto.spikyhair.service.ReservasService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservasService reservasService;

    public ReservaController(ReservasService reservasService) {
        this.reservasService = reservasService;
    }

    @GetMapping
    public ResponseEntity<List<ReservasDto>> getAllReservas() {
        return ResponseEntity.ok(reservasService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservasDto> getReservaById(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ReservasDto> createReserva(@RequestBody ReservasDto dto) {
        return ResponseEntity.ok(reservasService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservasDto> updateReserva(@PathVariable Long id, @RequestBody ReservasDto dto) {
        return ResponseEntity.ok(reservasService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        reservasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
