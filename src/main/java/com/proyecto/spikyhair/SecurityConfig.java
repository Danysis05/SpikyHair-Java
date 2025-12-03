package com.proyecto.spikyhair;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.proyecto.spikyhair.service.UsuarioDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/peluquerias/crear").permitAll()
                .requestMatchers(HttpMethod.POST, "/peluquerias/crear").permitAll()
                .requestMatchers("/", "/index", "/inicio").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/owners/**").hasAnyRole("DUEÑO", "ADMINISTRADOR", "USUARIO")
                .requestMatchers("/peluquerias/**").hasAnyRole("DUEÑO", "ADMINISTRADOR", "USUARIO")
                .requestMatchers("/usuarios/**").hasAnyRole("USUARIO", "ADMINISTRADOR", "DUEÑO")
                .requestMatchers("/reservas/eliminar/**").permitAll()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/auth/logueo", true)// ✔ Redirige según rol
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
