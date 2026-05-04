package com.cupotax.repositories;

import com.cupotax.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByTelefono(String telefono);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    List<User> findByRol(String rol);
    
    @Query("SELECT u FROM User u WHERE u.rol IN ('TAXISTA', 'BUSERO')")
    List<User> findAllDrivers();
    
    @Query("SELECT u FROM User u WHERE u.rol IN ('TAXISTA', 'BUSERO') AND u.activo = true")
    List<User> findAvailableDrivers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.activo = true")
    long countActiveUsers();
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(@Param("username") String username, @Param("password") String password);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.codigoRecuperacion = :codigo, u.codigoRecuperacionExpiracion = :expiracion WHERE u.email = :email")
    void setCodigoRecuperacion(@Param("email") String email, @Param("codigo") String codigo, @Param("expiracion") String expiracion);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.pinSeguridad = :pin WHERE u.username = :username")
    void updatePinSeguridad(@Param("username") String username, @Param("pin") String pin);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.latitud = :latitud, u.longitud = :longitud WHERE u.id = :id")
    void updateUbicacion(@Param("id") Long id, @Param("latitud") double latitud, @Param("longitud") double longitud);
}