package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Resena;

@Repository
public interface ResenasRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByPeluqueriaId(Long peluqueriaId);

    @Query("SELECT AVG(r.puntuacion) FROM Resena r WHERE r.peluqueria.id = :peluqueriaId")
    Double obtenerPromedioPorPeluqueria(@Param("peluqueriaId") Long peluqueriaId);

    // Query ahora retorna Resena directamente
    @Query("SELECT r FROM Resena r " +
           "WHERE r.peluqueria.id = :peluqueriaId " +
           "AND (" +
           "LOWER(r.comentario) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR STR(r.puntuacion) LIKE CONCAT('%', :q, '%') " +
           "OR LOWER(r.usuario.nombre) LIKE LOWER(CONCAT('%', :q, '%'))" +
           ")")
    List<Resena> buscarPorQuery(@Param("peluqueriaId") Long peluqueriaId, @Param("q") String q);
}
