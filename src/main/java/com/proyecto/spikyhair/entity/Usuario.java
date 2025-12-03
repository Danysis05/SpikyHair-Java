package com.proyecto.spikyhair.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, length = 10)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "contrasena", nullable = false, length = 100)
    private String contrasena;

    @Column(name = "telefono", nullable = true, length = 15)
    private String telefono;

    @Column(name = "imagen_perfil", nullable = true, length = 255)
    private String imagenPerfil;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resena> resenas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Peluqueria> peluquerias;

    @PrePersist
    private void prePersist() {
        this.fechaRegistro = LocalDate.now();
    }

    // ------------------- Getters y Setters -------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public List<Resena> getResenas() {
        return resenas;
    }

    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }

    public List<Peluqueria> getPeluquerias() {
        return peluquerias;
    }

    public void setPeluquerias(List<Peluqueria> peluquerias) {
        this.peluquerias = peluquerias;
    }
}
