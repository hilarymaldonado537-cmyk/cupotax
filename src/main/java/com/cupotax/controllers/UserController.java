package com.cupotax.controllers;

import com.cupotax.models.Trip;
import com.cupotax.models.User;
import com.cupotax.repositories.TripRepository;
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
@RequestMapping("/api")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @GetMapping("/billetera/saldo")
    public ResponseEntity<Map<String, Object>> getSaldo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Map<String, Object> response = new HashMap<>();
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        response.put("saldo", 45.30);
        return ResponseEntity.ok(response);
    }
    
    // ========== MÉTODOS ELIMINADOS (están en TripController) ==========
    // @GetMapping("/viajes/historial") -> movido a TripController
    // @PostMapping("/viajes/solicitar") -> movido a TripController
    // @GetMapping("/notificaciones") -> movido a NotificacionController
}