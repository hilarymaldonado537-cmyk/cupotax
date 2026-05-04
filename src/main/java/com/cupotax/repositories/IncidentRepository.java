package com.cupotax.repositories;

import com.cupotax.models.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    
    List<Incident> findByAtendidoOrderByFechaDesc(boolean atendido);
    
    List<Incident> findAllByOrderByFechaDesc();
    
    @Modifying
    @Transactional
    @Query("UPDATE Incident i SET i.atendido = true WHERE i.id = :id")
    void marcarComoAtendido(@Param("id") Long id);
}