package com.proyecto.spikyhair;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto.spikyhair.service.UsuarioService;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UsuarioService usuarioService) {
        return args -> {
            usuarioService.crearAdministradorInicial();
        };
    }
}