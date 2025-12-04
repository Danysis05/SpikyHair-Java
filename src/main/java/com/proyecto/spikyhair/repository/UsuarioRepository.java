package com.proyecto.spikyhair.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByEmail(String email);
        long countByRol_Id(Long rolId);
            @Query("SELECT MONTH(u.fechaRegistro), COUNT(u) " +
           "FROM Usuario u " +
           "WHERE YEAR(u.fechaRegistro) = :year " +
           "GROUP BY MONTH(u.fechaRegistro) " +
           "ORDER BY MONTH(u.fechaRegistro)")
    List<Object[]> countUsuariosByMonth(@Param("year") int year);
    @Query("""
    SELECT u FROM Usuario u
    WHERE 
        LOWER(u.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(u.telefono) LIKE LOWER(CONCAT('%', :query, '%')) OR
        LOWER(u.rol.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Usuario> buscarPorQuery(@Param("query") String query);
    @Query("SELECT u FROM Usuario u JOIN u.peluquerias p WHERE p.id = :peluqueriaId")
Usuario findUsuarioByPeluqueriaId(@Param("peluqueriaId") Long peluqueriaId);


    
}
