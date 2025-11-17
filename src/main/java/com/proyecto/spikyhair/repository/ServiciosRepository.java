package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Servicios;

@Repository
public interface ServiciosRepository extends JpaRepository<Servicios, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    boolean existsByNombreIgnoreCase(String nombre);
    List<Servicios> findByPeluqueriaId(Long peluqueriaId);

}
