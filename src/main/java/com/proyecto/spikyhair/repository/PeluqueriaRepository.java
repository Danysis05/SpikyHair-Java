package com.proyecto.spikyhair.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Peluqueria;

@Repository
public interface PeluqueriaRepository extends JpaRepository<Peluqueria, Long> {
    Optional<Peluqueria> findByUsuarioId(Long usuarioId);
    @Query("""
    SELECT p FROM Peluqueria p
    WHERE 
        LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.direccion) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.telefono) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.horarioApertura) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.horarioCierre) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Peluqueria> buscarPorQuery(@Param("query") String query);

@Query(value = """
    SELECT 
        p.id AS id,
        p.nombre AS nombre,
        p.direccion AS direccion,
        p.descripcion AS descripcion,
        p.imagen_url AS imagenUrl,
        COALESCE(AVG(r.puntuacion), 0) AS promedio
    FROM peluqueria p
    LEFT JOIN resena r ON r.peluqueria_id = p.id
    GROUP BY p.id
    ORDER BY promedio DESC
    LIMIT 5
    """, nativeQuery = true)
List<Object[]> findTop5PeluqueriasWithPromedioRaw();



}
