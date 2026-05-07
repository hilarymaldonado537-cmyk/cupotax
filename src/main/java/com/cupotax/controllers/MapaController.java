package com.cupotax.controllers;

import com.cupotax.models.User;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/mapa")
public class MapaController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/ubicaciones")
    public Map<String, Object> getUbicaciones() {
        Map<String, Object> response = new HashMap<>();
        
        // Taxis (todos)
        List<User> taxis = userRepository.findByRol("TAXISTA");
        
        // Buses (todos)
        List<User> buses = userRepository.findByRol("BUSERO");
        
        response.put("taxis", taxis);
        response.put("buses", buses);
        
        return response;
    }
}