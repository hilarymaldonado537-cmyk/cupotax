package com.cupotax.repositories;

import com.cupotax.models.Trip;
import com.cupotax.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    List<Trip> findByUsuario(User usuario);
    List<Trip> findByConductor(User conductor);
    List<Trip> findByEstado(String estado);
    List<Trip> findByEstadoOrderByFechaSolicitudDesc(String estado);
    
    @Query("SELECT t FROM Trip t WHERE t.conductor = :conductor AND t.estado = 'pendiente'")
    List<Trip> findPendingTripsByConductor(@Param("conductor") User conductor);
    
    @Query("SELECT t FROM Trip t WHERE t.usuario = :usuario ORDER BY t.fechaSolicitud DESC")
    List<Trip> findRecentTripsByUser(@Param("usuario") User usuario);
    
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.conductor = :conductor AND t.estado = 'completado'")
    long countCompletedTripsByConductor(@Param("conductor") User conductor);
    
    @Query("SELECT SUM(t.tarifa) FROM Trip t WHERE t.conductor = :conductor AND t.estado = 'completado'")
    Double sumEarningsByConductor(@Param("conductor") User conductor);
    
    @Query("SELECT t FROM Trip t WHERE t.fechaSolicitud BETWEEN :start AND :end")
    List<Trip> findTripsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}