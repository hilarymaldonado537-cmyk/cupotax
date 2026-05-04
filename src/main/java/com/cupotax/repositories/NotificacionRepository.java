package com.cupotax.repositories;

import com.cupotax.models.Notificacion;
import com.cupotax.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByUsuarioOrderByFechaDesc(User usuario);
    
    long countByUsuarioAndLeidaFalse(User usuario);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id = :id")
    void marcarComoLeida(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario = :usuario AND n.leida = false")
    void marcarTodasComoLeidas(@Param("usuario") User usuario);
}