package com.cupotax.controllers;

import com.cupotax.models.User;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class ApiController {
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/reservas")
    public ResponseEntity<Map<String, Object>> guardarReserva(@RequestBody Map<String, Object> reserva) {
        System.out.println("Reserva recibida: " + reserva);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Reserva confirmada");
        respuesta.put("codigo", reserva.get("codigo"));
        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/drivers")
    public List<User> getAllDrivers() {
        return userRepository.findAllDrivers();
    }
    
    @GetMapping("/driver/{id}")
    public ResponseEntity<User> getDriver(@PathVariable Long id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/ayuda")
    public ResponseEntity<Map<String, String>> enviarAyuda(@RequestBody Map<String, String> reporte) {
        System.out.println("Reporte de ayuda: " + reporte);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "recibido");
        return ResponseEntity.ok(respuesta);
    }
}