package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Reserva;

@Repository
public interface ReservasRepository extends JpaRepository<Reserva, Long> { 

    List<Reserva> findByUsuarioId(Long usuarioId);
}
