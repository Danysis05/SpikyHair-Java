package com.proyecto.spikyhair.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario

}
