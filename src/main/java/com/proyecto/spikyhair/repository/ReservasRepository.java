package com.proyecto.spikyhair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proyecto.spikyhair.entity.Reserva;
import com.proyecto.spikyhair.enums.Estado;

@Repository
public interface ReservasRepository extends JpaRepository<Reserva, Long> { 
    List<Reserva> findByEstado(Estado estado);
    List<Reserva> findByUsuarioId(Long usuarioId);
    long countByEstado(Estado estado);
    List<Reserva> findByPeluqueriaId(Long peluqueriaId);

    @Query("SELECT r.servicios.nombre, COUNT(r) FROM Reserva r GROUP BY r.servicios.nombre")
    List<Object[]> contarReservasPorServicio();

    @Query("SELECT MONTH(r.fecha), COUNT(r) FROM Reserva r GROUP BY MONTH(r.fecha) ORDER BY MONTH(r.fecha)")
    List<Object[]> contarReservasPorMes();

    @Query("SELECT SUM(s.precio) FROM Reserva r JOIN r.servicios s WHERE r.estado = com.proyecto.spikyhair.enums.Estado.REALIZADA")
    Double obtenerIngresosTotales();
   @Query("SELECT YEAR(r.fecha) AS anio, MONTH(r.fecha) AS mes, SUM(s.precio) AS total " +
       "FROM Reserva r " +
       "JOIN r.servicios s " +
       "GROUP BY YEAR(r.fecha), MONTH(r.fecha) " +
       "ORDER BY anio, mes")
List<Object[]> obtenerIngresosPorAnioMes();



}
