package com.cupotax.controllers;

import com.cupotax.models.Notificacion;
import com.cupotax.models.Trip;
import com.cupotax.models.User;
import com.cupotax.repositories.NotificacionRepository;
import com.cupotax.repositories.TripRepository;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/viajes")
public class TripController {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    // ========== USUARIO: Solicitar un viaje ==========
    @PostMapping("/solicitar")
    public ResponseEntity<Map<String, Object>> solicitarViaje(@RequestBody Map<String, Object> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        User usuario = userOpt.get();
        
        String origen = (String) request.get("origen");
        String destino = (String) request.get("destino");
        Double tarifa = ((Number) request.get("tarifaEstimada")).doubleValue();
        String tipoServicio = (String) request.get("tipoServicio");
        String tipoViaje = (String) request.get("tipoViaje");
        
        Trip trip = new Trip();
        trip.setUsuario(usuario);
        trip.setOrigen(origen);
        trip.setDestino(destino);
        trip.setTarifa(tarifa);
        trip.setTipoServicio(tipoServicio);
        trip.setTipoViaje(tipoViaje);
        trip.setEstado("pendiente");
        trip.setFechaSolicitud(LocalDateTime.now());
        
        tripRepository.save(trip);
        
        response.put("success", true);
        response.put("message", "Viaje solicitado correctamente");
        response.put("tripId", trip.getId());
        
        return ResponseEntity.ok(response);
    }
    
    // ========== TAXISTA: Obtener viajes pendientes ==========
    @GetMapping("/pendientes")
    public ResponseEntity<List<Trip>> getViajesPendientes(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Trip> viajesPendientes = tripRepository.findByEstadoOrderByFechaSolicitudDesc("pendiente");
        return ResponseEntity.ok(viajesPendientes);
    }
    
    // ========== TAXISTA: Aceptar un viaje ==========
    @PutMapping("/{id}/aceptar")
    public ResponseEntity<Map<String, Object>> aceptarViaje(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<User> taxistaOpt = userRepository.findById(userId);
        if (taxistaOpt.isEmpty()) {
            response.put("error", "Taxista no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            response.put("error", "Viaje no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Trip trip = tripOpt.get();
        
        if (!"pendiente".equals(trip.getEstado())) {
            response.put("error", "Este viaje ya fue aceptado o cancelado");
            return ResponseEntity.badRequest().body(response);
        }
        
        User taxista = taxistaOpt.get();
        trip.setConductor(taxista);
        trip.setEstado("asignado");
        trip.setFechaInicio(LocalDateTime.now());
        tripRepository.save(trip);
        
        // Crear notificación para el usuario
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(trip.getUsuario());
        notificacion.setTitulo("🚖 Viaje aceptado");
        notificacion.setMensaje("Tu viaje de " + trip.getOrigen() + " a " + trip.getDestino() + 
                                " ha sido aceptado por " + taxista.getNombreCompleto() + 
                                " (Placa: " + (taxista.getPlaca() != null ? taxista.getPlaca() : "N/A") + ").");
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());
        notificacionRepository.save(notificacion);
        
        response.put("success", true);
        response.put("message", "Viaje aceptado correctamente");
        response.put("tripId", trip.getId());
        response.put("conductor", taxista.getNombreCompleto());
        response.put("placa", taxista.getPlaca());
        
        return ResponseEntity.ok(response);
    }
    
    // ========== TAXISTA: Rechazar un viaje ==========
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Map<String, Object>> rechazarViaje(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            response.put("error", "Viaje no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Trip trip = tripOpt.get();
        
        if (!"pendiente".equals(trip.getEstado())) {
            response.put("error", "Este viaje ya fue aceptado o cancelado");
            return ResponseEntity.badRequest().body(response);
        }
        
        trip.setEstado("rechazado");
        tripRepository.save(trip);
        
        // Crear notificación de rechazo
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(trip.getUsuario());
        notificacion.setTitulo("❌ Viaje rechazado");
        notificacion.setMensaje("Tu viaje de " + trip.getOrigen() + " a " + trip.getDestino() + 
                                " fue rechazado. Por favor, solicita otro viaje.");
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());
        notificacionRepository.save(notificacion);
        
        response.put("success", true);
        response.put("message", "Viaje rechazado");
        
        return ResponseEntity.ok(response);
    }
    
    // ========== USUARIO/TAXISTA: Historial de viajes ==========
    @GetMapping("/historial")
    public ResponseEntity<List<Trip>> getHistorialViajes(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        List<Trip> viajes;
        
        if ("TAXISTA".equals(user.getRol()) || "BUSERO".equals(user.getRol())) {
            viajes = tripRepository.findByConductor(user);
        } else {
            viajes = tripRepository.findByUsuario(user);
        }
        
        return ResponseEntity.ok(viajes);
    }
    
    // ========== TAXISTA: Obtener viajes asignados a él ==========
    @GetMapping("/mis-viajes")
    public ResponseEntity<List<Trip>> getMisViajesAsignados(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<User> taxistaOpt = userRepository.findById(userId);
        if (taxistaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Trip> viajes = tripRepository.findByConductor(taxistaOpt.get());
        return ResponseEntity.ok(viajes);
    }
    
    // ========== USUARIO: Cancelar un viaje pendiente ==========
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarViaje(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            response.put("error", "Viaje no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Trip trip = tripOpt.get();
        
        if (!"pendiente".equals(trip.getEstado())) {
            response.put("error", "Solo puedes cancelar viajes pendientes");
            return ResponseEntity.badRequest().body(response);
        }
        
        trip.setEstado("cancelado");
        tripRepository.save(trip);
        
        response.put("success", true);
        response.put("message", "Viaje cancelado correctamente");
        
        return ResponseEntity.ok(response);
    }
    
    // ========== TAXISTA: Completar un viaje (con calificación) ==========
    @PutMapping("/{id}/completar")
    public ResponseEntity<Map<String, Object>> completarViaje(@PathVariable Long id, 
                                                               @RequestParam(required = false) Integer calificacion,
                                                               @RequestParam(required = false) String comentario,
                                                               HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            response.put("error", "Viaje no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Trip trip = tripOpt.get();
        
        if (!"asignado".equals(trip.getEstado())) {
            response.put("error", "Solo puedes completar viajes asignados");
            return ResponseEntity.badRequest().body(response);
        }
        
        trip.setEstado("completado");
        trip.setFechaFin(LocalDateTime.now());
        if (calificacion != null) {
            trip.setCalificacion(calificacion);
        }
        if (comentario != null) {
            trip.setComentario(comentario);
        }
        tripRepository.save(trip);
        
        response.put("success", true);
        response.put("message", "Viaje completado correctamente");
        
        return ResponseEntity.ok(response);
    }
}