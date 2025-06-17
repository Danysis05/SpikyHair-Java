package com.proyecto.spikyhair.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "servicios")
@Data


public class Servicios {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    
    @Column(name = "id", unique = true, nullable = false, length = 10)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "duracion", nullable = false, length = 50)
    private String duracion;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "precio", nullable = false, length = 10)
    private Double precio;

    @Column(name = "imagen", nullable = true, length = 255)
    private String imagen;

    @OneToMany(mappedBy = "servicios")

    private List<Reserva> reservas;

}
