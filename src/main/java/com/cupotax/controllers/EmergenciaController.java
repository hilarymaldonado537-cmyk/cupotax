package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/emergencia")
@CrossOrigin(origins = "*")
public class EmergenciaController {

    @Autowired
    private DatabaseReference database;

    @PostMapping("/reportar")
    public ResponseEntity<?> reportarEmergencia(@RequestBody Map<String, Object> emergencia) {
        try {
            String emergenciaId = database.child("emergencias").push().getKey();
            emergencia.put("fecha", new Date().toString());
            emergencia.put("estado", "pendiente");
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("emergencias").child(emergenciaId).setValue(emergencia, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();

            // Notificar al admin
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("titulo", "🚨 Nueva emergencia reportada");
            notificacion.put("mensaje", "Se ha reportado una emergencia: " + emergencia.get("tipo"));
            notificacion.put("fecha", new Date().toString());
            notificacion.put("leida", false);
            database.child("admin").child("notificaciones").push().setValueAsync(notificacion);

            return ResponseEntity.ok(Map.of(
                "message", "Emergencia reportada",
                "emergenciaId", emergenciaId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarEmergencias() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("emergencias").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> emergencias = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> e = new HashMap<>();
                        e.put("id", child.getKey());
                        if (child.getValue() != null) {
                            e.putAll((Map<String, Object>) child.getValue());
                        }
                        emergencias.add(e);
                    }
                    future.complete(emergencias);
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

    @GetMapping("/user/{uid}")
    public ResponseEntity<?> getEmergenciasByUser(@PathVariable String uid) {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("emergencias").orderByChild("usuarioId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> emergencias = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> e = new HashMap<>();
                        e.put("id", child.getKey());
                        if (child.getValue() != null) {
                            e.putAll((Map<String, Object>) child.getValue());
                        }
                        emergencias.add(e);
                    }
                    future.complete(emergencias);
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