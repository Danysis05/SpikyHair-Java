package com.proyecto.spikyhair.entity;

import java.time.LocalDate;

import com.proyecto.spikyhair.enums.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reserva")
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private String hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.PENDIENTE; // Valor inicial

    @Column(name = "duracion", nullable = false)
    private String duracion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "servicios_id", nullable = false)
    private Servicios servicios;

    // Estilista opcional
    @ManyToOne
    @JoinColumn(name = "estilista_id", nullable = true)
    private Estilista estilista;

    @ManyToOne
    @JoinColumn(name = "peluqueria_id", nullable = false)
    private Peluqueria peluqueria;
}
