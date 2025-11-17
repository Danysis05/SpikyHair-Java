package com.proyecto.spikyhair;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RolesSingleton {

    private final List<String> rolesBase = List.of("ADMINISTRADOR", "USUARIO", "DUEÑO");

    public List<String> getRolesBase() {
        return rolesBase;
    }
}
