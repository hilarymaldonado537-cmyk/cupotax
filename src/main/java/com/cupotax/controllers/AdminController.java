package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private DatabaseReference database;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> users = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", child.getKey());
                        if (child.getValue() != null) {
                            user.putAll((Map<String, Object>) child.getValue());
                        }
                        users.add(user);
                    }
                    future.complete(users);
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

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
            Map<String, Object> stats = new HashMap<>();
            
            database.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    int total = 0, taxistas = 0, buseros = 0, admins = 0, usuarios = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        total++;
                        Map<String, Object> data = (Map<String, Object>) child.getValue();
                        if (data != null) {
                            String role = (String) data.get("rol");
                            if ("TAXISTA".equals(role)) taxistas++;
                            else if ("BUSERO".equals(role)) buseros++;
                            else if ("ADMIN".equals(role)) admins++;
                            else if ("USUARIO".equals(role)) usuarios++;
                        }
                    }
                    stats.put("totalUsuarios", total);
                    stats.put("taxistas", taxistas);
                    stats.put("buseros", buseros);
                    stats.put("admins", admins);
                    stats.put("usuarios", usuarios);
                    future.complete(stats);
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

    @GetMapping("/viajes")
    public ResponseEntity<?> getAllViajes() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("viajes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> viajes = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> viaje = new HashMap<>();
                        viaje.put("id", child.getKey());
                        if (child.getValue() != null) {
                            viaje.putAll((Map<String, Object>) child.getValue());
                        }
                        viajes.add(viaje);
                    }
                    future.complete(viajes);
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

    @GetMapping("/notificaciones")
    public ResponseEntity<?> getAdminNotifications() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("admin").child("notificaciones").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> notifs = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> n = new HashMap<>();
                        n.put("id", child.getKey());
                        if (child.getValue() != null) {
                            n.putAll((Map<String, Object>) child.getValue());
                        }
                        notifs.add(n);
                    }
                    future.complete(notifs);
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

    @GetMapping("/incidentes")
    public ResponseEntity<?> getAllIncidentes() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("incidentes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> incidentes = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> incidente = new HashMap<>();
                        incidente.put("id", child.getKey());
                        if (child.getValue() != null) {
                            incidente.putAll((Map<String, Object>) child.getValue());
                        }
                        incidentes.add(incidente);
                    }
                    future.complete(incidentes);
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