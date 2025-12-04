package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.DTO.ServiciosDto;
import com.proyecto.spikyhair.entity.Servicios;

@Repository
public interface ServiciosRepository extends JpaRepository<Servicios, Long> {

@Query("SELECT new com.proyecto.spikyhair.DTO.ServiciosDto(" +
       "s.id, s.nombre, s.duracion, s.descripcion, s.precio, s.imagen, s.peluqueria.id) " +
       "FROM Servicios s " +   // <- debe coincidir con la clase: Servicios
       "WHERE s.peluqueria.id = :peluqueriaId " +
       "AND (" +
       "LOWER(s.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
       "OR LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :q, '%')) " +
       "OR CAST(s.precio AS string) LIKE CONCAT('%', :q, '%') " +
       "OR LOWER(s.duracion) LIKE LOWER(CONCAT('%', :q, '%'))" +
       ")")
List<ServiciosDto> buscarPorQuery(@Param("peluqueriaId") Long peluqueriaId, @Param("q") String q);




    // Aquí puedes agregar métodos personalizados si es necesario
    boolean existsByNombreIgnoreCase(String nombre);
    List<Servicios> findByPeluqueriaId(Long peluqueriaId);
     boolean existsByNombreIgnoreCaseAndPeluqueriaId(String nombre, Long peluqueriaId);
     

}
