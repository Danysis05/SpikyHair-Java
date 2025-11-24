package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Estilista;

@Repository
public interface EstilistaRepository  extends JpaRepository<Estilista, Long> {
    List<Estilista> findByPeluqueriaId(Long peluqueriaId);
    boolean existsByNombreIgnoreCaseAndPeluqueriaId(String nombre, Long peluqueriaId);
}
