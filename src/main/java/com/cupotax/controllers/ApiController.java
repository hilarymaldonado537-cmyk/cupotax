package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private DatabaseReference database;

    @GetMapping("/users/{uid}")
    public ResponseEntity<?> getUser(@PathVariable String uid) {
        try {
            CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
            database.child("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", snapshot.getKey());
                    if (snapshot.getValue() != null) {
                        data.putAll((Map<String, Object>) snapshot.getValue());
                    }
                    future.complete(data);
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

    @GetMapping("/trips/user/{uid}")
    public ResponseEntity<?> getUserTrips(@PathVariable String uid) {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("viajes").orderByChild("usuarioId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @PostMapping("/trips")
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
            return ResponseEntity.ok(Map.of("message", "Viaje creado", "tripId", tripId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}