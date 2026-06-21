package com.cupotax.controllers;

import com.cupotax.models.Trip;
import com.cupotax.models.User;
import com.cupotax.repositories.TripRepository;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @GetMapping("")
    public String adminPanel() {
        return "admin";
    }
    
    @GetMapping("/users")
    @ResponseBody
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/lista-conductores")
    @ResponseBody
    public List<User> getDrivers() {
        return userRepository.findAllDrivers();
    }
    
    @GetMapping("/trips")
    @ResponseBody
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }
    
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", userRepository.count());
        stats.put("totalTaxistas", userRepository.findByRol("TAXISTA").size());
        stats.put("totalBuseros", userRepository.findByRol("BUSERO").size());
        stats.put("totalViajes", tripRepository.count());
        
        Double ingresos = tripRepository.findAll().stream()
                .mapToDouble(Trip::getTarifa)
                .sum();
        stats.put("totalIngresos", ingresos);
        
        return stats;
    }
    
    @GetMapping("/user/{id}")
    @ResponseBody
    public Optional<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id);
    }
    
    @PostMapping("/user/toggle/{id}")
    @ResponseBody
    public String toggleUserStatus(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActivo(!user.isActivo());
            userRepository.save(user);
            return "Usuario actualizado";
        }
        return "Usuario no encontrado";
    }
}