package com.cupotax.controllers;

import com.cupotax.models.Notificacion;
import com.cupotax.models.User;
import com.cupotax.repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @GetMapping
    public ResponseEntity<?> getNotificaciones(HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
        long noLeidas = notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
        
        Map<String, Object> response = new HashMap<>();
        response.put("notificaciones", notificaciones);
        response.put("noLeidas", noLeidas);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/contador")
    public ResponseEntity<?> getContador(HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        long noLeidas = notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
        return ResponseEntity.ok(Map.of("noLeidas", noLeidas));
    }
    
    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        notificacionRepository.marcarComoLeida(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @PutMapping("/marcar-todas")
    public ResponseEntity<?> marcarTodasComoLeidas(HttpSession session) {
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        notificacionRepository.marcarTodasComoLeidas(usuario);
        return ResponseEntity.ok(Map.of("success", true));
    }
}