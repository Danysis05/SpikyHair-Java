package com.proyecto.spikyhair.entity;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

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

    @ManyToOne
    @JoinColumn(name = "peluqueria_id", nullable = false)
    private Peluqueria peluqueria;
    @OneToMany(mappedBy = "servicios")
    private List<Reserva> reservas;

}
