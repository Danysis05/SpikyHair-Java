package com.proyecto.spikyhair.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.spikyhair.DTO.UsuarioDto;
import com.proyecto.spikyhair.entity.Rol;
import com.proyecto.spikyhair.entity.Usuario;
import com.proyecto.spikyhair.repository.RolRepository;
import com.proyecto.spikyhair.repository.UsuarioRepository;
import com.proyecto.spikyhair.service.DAO.Idao;

import jakarta.annotation.PostConstruct;

@Service
public class UsuarioService implements Idao<Usuario, Long, UsuarioDto> {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
    }

    @Override
    public List<UsuarioDto> getAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto getById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        return modelMapper.map(usuario, UsuarioDto.class);
    }

    @Override
    public UsuarioDto save(UsuarioDto dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);

        Rol rolUsuario = rolRepository.findByNombre("USUARIO")
            .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));
        usuario.setRol(rolUsuario);

        usuario.setImagenPerfil(dto.getImagenPerfil()); // 👈 AÑADIR ESTO

        String hashed = passwordEncoder.encode(dto.getContrasena());
        usuario.setContrasena(hashed);

        Usuario creado = usuarioRepository.save(usuario);
        return modelMapper.map(creado, UsuarioDto.class);
    }

    @Override
    public UsuarioDto update(Long id, UsuarioDto dto) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow();

        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());

        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            String hashed = passwordEncoder.encode(dto.getContrasena());
            existente.setContrasena(hashed);
        }

        existente.setImagenPerfil(dto.getImagenPerfil()); // 👈 AÑADIR ESTO

        Usuario actualizado = usuarioRepository.save(existente);
        return modelMapper.map(actualizado, UsuarioDto.class);
    }



    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
    @Override
    public long count() {
        return usuarioRepository.count();
    }
    public Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String email = auth.getName(); 
        return usuarioRepository.findByEmail(email).orElse(null);
    }
    
    public void actualizarRol(Long usuarioId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
            
        Rol nuevoRol = rolRepository.findById(rolId)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + rolId));
            
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    public void crearAdministradorInicial() {
    String email = "admin@admin.com";
    if (usuarioRepository.findByEmail(email).isEmpty()) {
        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setEmail(email);
        admin.setContrasena(passwordEncoder.encode("admin123")); // Contraseña hasheada

        Rol rolAdmin = rolRepository.findByNombre("ADMINISTRADOR")
                .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
        admin.setRol(rolAdmin);

        admin.setImagenPerfil("admin.png"); // Puedes dejarlo nulo o usar una imagen por defecto
        usuarioRepository.save(admin);

        System.out.println("✅ Usuario ADMINISTRADOR creado correctamente.");
    } else {
        System.out.println("ℹ️ El usuario ADMINISTRADOR ya existe.");
    }
}
public List<UsuarioDto> filtrarUsuarios(String nombreOEmail, String rolNombre) {
    return usuarioRepository.findAll()
        .stream()
        .filter(usuario -> {
            boolean coincideNombreOEmail = (nombreOEmail == null || nombreOEmail.isBlank()) ||
                    usuario.getNombre().toLowerCase().contains(nombreOEmail.toLowerCase()) ||
                    usuario.getEmail().toLowerCase().contains(nombreOEmail.toLowerCase());

            boolean coincideRol = (rolNombre == null || rolNombre.isBlank()) ||
                    (usuario.getRol() != null && usuario.getRol().getNombre().equalsIgnoreCase(rolNombre));

            return coincideNombreOEmail && coincideRol;
        })
        .map(usuario -> modelMapper.map(usuario, UsuarioDto.class))
        .collect(Collectors.toList());
}

@PostConstruct
public void initRoles() {
    // Crear ADMINISTRADOR primero
    Optional<Rol> admin = rolRepository.findByNombre("ADMINISTRADOR");
    if (admin.isEmpty()) {
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ADMINISTRADOR");
        rolRepository.save(rolAdmin);
        System.out.println("✅ Rol ADMINISTRADOR creado correctamente.");
    }

    // Crear USUARIO después
    Optional<Rol> user = rolRepository.findByNombre("USUARIO");
    if (user.isEmpty()) {
        Rol rolUsuario = new Rol();
        rolUsuario.setNombre("USUARIO");
        rolRepository.save(rolUsuario);
        System.out.println("✅ Rol USUARIO creado correctamente.");
    }
}



    public long countAdmins() {
        return usuarioRepository.countByRol_Id(1L);
    }

    public long countUsers() {
        return usuarioRepository.countByRol_Id(2L);
    }

    
public List<String> obtenerMesesUsuarios() {
    return Arrays.asList(
        "Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
    );
}


public List<Long> obtenerUsuariosPorMes(int year) {
    List<Object[]> rows = usuarioRepository.countUsuariosByMonth(year);
    Map<Integer, Long> map = new HashMap<>();
    for (Object[] row : rows) {
        Integer mes = ((Number) row[0]).intValue(); // 1..12
        Long total = ((Number) row[1]).longValue();
        map.put(mes, total);
    }

    // Rellenar lista con 12 posiciones (0 si no hay usuarios en el mes)
    List<Long> resultados = new ArrayList<>();
    for (int m = 1; m <= 12; m++) {
        resultados.add(map.getOrDefault(m, 0L));
    }
    return resultados;
}



    
}





