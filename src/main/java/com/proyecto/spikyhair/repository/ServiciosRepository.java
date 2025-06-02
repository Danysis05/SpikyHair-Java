package com.proyecto.spikyhair.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Servicios;

@Repository
public interface ServiciosRepository extends JpaRepository<Servicios, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario

}
