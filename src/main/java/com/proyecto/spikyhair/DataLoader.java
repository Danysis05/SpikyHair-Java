package com.proyecto.spikyhair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.repository.RolRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final RolesSingleton rolesSingleton;

    public DataLoader(RolRepository rolRepository, RolesSingleton rolesSingleton) {
        this.rolRepository = rolRepository;
        this.rolesSingleton = rolesSingleton;
    }

@Override
public void run(String... args) {
    if (rolRepository.findByNombre("ADMINISTRADOR").isEmpty()) {
        rolRepository.save(new Rol("ADMINISTRADOR"));
        System.out.println("Rol ADMINISTRADOR creado");
    }
    if (rolRepository.findByNombre("USUARIO").isEmpty()) {
        rolRepository.save(new Rol("USUARIO"));
        System.out.println("Rol USUARIO creado");
    }
}

}
