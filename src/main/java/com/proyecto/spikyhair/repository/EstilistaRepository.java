package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Estilista;

@Repository
public interface EstilistaRepository  extends JpaRepository<Estilista, Long> {
   @Query("SELECT e FROM Estilista e " +
       "WHERE e.peluqueria.id = :peluqueriaId " +
       "AND (" +
       "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
       "OR LOWER(e.especialidad) LIKE LOWER(CONCAT('%', :q, '%'))" +
       ")")
List<Estilista> buscarPorQuery(@Param("peluqueriaId") Long peluqueriaId, @Param("q") String q);

    List<Estilista> findByPeluqueriaId(Long peluqueriaId);
    boolean existsByNombreIgnoreCaseAndPeluqueriaId(String nombre, Long peluqueriaId);
}
