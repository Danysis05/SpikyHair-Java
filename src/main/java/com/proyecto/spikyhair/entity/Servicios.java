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

public String getDuracion() {
    return duracion;
}

public void setDuracion(String duracion) {
    this.duracion = duracion;
}

public String getDescripcion() {
    return descripcion;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}

public Double getPrecio() {
    return precio;
}

public void setPrecio(Double precio) {
    this.precio = precio;
}

public String getImagen() {
    return imagen;
}

public void setImagen(String imagen) {
    this.imagen = imagen;
}

public Peluqueria getPeluqueria() {
    return peluqueria;
}

public void setPeluqueria(Peluqueria peluqueria) {
    this.peluqueria = peluqueria;
}

public List<Reserva> getReservas() {
    return reservas;
}

public void setReservas(List<Reserva> reservas) {
    this.reservas = reservas;
}

}
