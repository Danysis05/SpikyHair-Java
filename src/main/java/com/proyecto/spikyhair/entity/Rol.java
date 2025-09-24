package com.proyecto.spikyhair.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rol")
@Data
public class Rol {
    public Rol() {
    }

    public Rol(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Constructor extra para crear rol solo con nombre
    public Rol(String nombre) {
        this.nombre = nombre;
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", unique=true, nullable=false, length=10)
    private Long id;

    @Column(name = "nombre", nullable=false, length=50)
    private String nombre;

    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;

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
}
