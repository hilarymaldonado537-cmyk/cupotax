package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private DatabaseReference database;

    // ========== ELIMINADO: getUserTrips (DUPLICADO EN ApiController) ==========

    @GetMapping("/driver/{uid}")
    public ResponseEntity<?> getDriverTrips(@PathVariable String uid) {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("viajes").orderByChild("taxistaId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> trips = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> trip = new HashMap<>();
                        trip.put("id", child.getKey());
                        if (child.getValue() != null) {
                            trip.putAll((Map<String, Object>) child.getValue());
                        }
                        trips.add(trip);
                    }
                    future.complete(trips);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            return ResponseEntity.ok(future.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/busero/{uid}")
    public ResponseEntity<?> getBuseroTrips(@PathVariable String uid) {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("viajes").orderByChild("buseroId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> trips = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> trip = new HashMap<>();
                        trip.put("id", child.getKey());
                        if (child.getValue() != null) {
                            trip.putAll((Map<String, Object>) child.getValue());
                        }
                        trips.add(trip);
                    }
                    future.complete(trips);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            return ResponseEntity.ok(future.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody Map<String, Object> tripData) {
        try {
            String tripId = database.child("viajes").push().getKey();
            tripData.put("fechaCreacion", new Date().toString());
            tripData.put("estado", "pendiente");
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("viajes").child(tripId).setValue(tripData, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();

            // Crear notificación
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("usuarioId", tripData.get("usuarioId"));
            notificacion.put("titulo", "🚗 Viaje solicitado");
            notificacion.put("mensaje", "Tu viaje ha sido registrado. Buscando conductor...");
            notificacion.put("fecha", new Date().toString());
            notificacion.put("leida", false);
            notificacion.put("tripId", tripId);
            database.child("notificaciones").push().setValueAsync(notificacion);

            return ResponseEntity.ok(Map.of("message", "Viaje creado", "tripId", tripId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTrip(@PathVariable String tripId, @RequestBody Map<String, Object> updates) {
        try {
            updates.put("fechaActualizacion", new Date().toString());
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("viajes").child(tripId).updateChildren(updates, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Viaje actualizado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable String tripId) {
        try {
            CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
            database.child("viajes").child(tripId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> trip = new HashMap<>();
                    trip.put("id", snapshot.getKey());
                    if (snapshot.getValue() != null) {
                        trip.putAll((Map<String, Object>) snapshot.getValue());
                    }
                    future.complete(trip);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            return ResponseEntity.ok(future.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> getPendingTrips() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("viajes").orderByChild("estado").equalTo("pendiente").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> trips = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> trip = new HashMap<>();
                        trip.put("id", child.getKey());
                        if (child.getValue() != null) {
                            trip.putAll((Map<String, Object>) child.getValue());
                        }
                        trips.add(trip);
                    }
                    future.complete(trips);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            return ResponseEntity.ok(future.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
