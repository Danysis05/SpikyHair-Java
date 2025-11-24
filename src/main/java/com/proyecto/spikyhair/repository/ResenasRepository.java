package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Resena;

@Repository
public interface ResenasRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByPeluqueriaId(Long peluqueriaId);
@Query("SELECT AVG(r.puntuacion) FROM Resena r WHERE r.peluqueria.id = :peluqueriaId")
Double obtenerPromedioPorPeluqueria(Long peluqueriaId);

}
