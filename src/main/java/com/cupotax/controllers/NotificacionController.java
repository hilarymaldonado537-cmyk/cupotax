package com.cupotax.controllers;

import com.cupotax.models.Notificacion;
import com.cupotax.models.User;
import com.cupotax.repositories.NotificacionRepository;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<?> getNotificaciones(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
        
        User usuario = userOpt.get();
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
        long noLeidas = notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
        
        Map<String, Object> response = new HashMap<>();
        response.put("notificaciones", notificaciones);
        response.put("noLeidas", noLeidas);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/contador")
    public ResponseEntity<?> getContador(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
        
        long noLeidas = notificacionRepository.countByUsuarioAndLeidaFalse(userOpt.get());
        return ResponseEntity.ok(Map.of("noLeidas", noLeidas));
    }
    
    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        notificacionRepository.marcarComoLeida(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @PutMapping("/marcar-todas")
    public ResponseEntity<?> marcarTodasComoLeidas(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
        
        notificacionRepository.marcarTodasComoLeidas(userOpt.get());
        return ResponseEntity.ok(Map.of("success", true));
    }
}