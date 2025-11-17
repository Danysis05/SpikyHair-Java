package com.proyecto.spikyhair.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Peluqueria;

@Repository
public interface PeluqueriaRepository extends JpaRepository<Peluqueria, Long> {
    Optional<Peluqueria> findByUsuarioId(Long usuarioId);
}
