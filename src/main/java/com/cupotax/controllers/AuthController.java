package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private DatabaseReference database;

    // Verificar token (solo valida que exista el usuario en Firebase)
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
        }

        String token = authHeader.substring(7);
        
        try {
            CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
            database.child("usuarios").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
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

            Map<String, Object> user = future.get();
            if (user == null || user.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
            }

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "uid", user.get("uid"),
                "role", user.getOrDefault("rol", "USUARIO"),
                "user", user
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al verificar: " + e.getMessage()));
        }
    }

    // Obtener usuario por UID
    @GetMapping("/user/{uid}")
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

            Map<String, Object> user = future.get();
            if (user == null || user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}