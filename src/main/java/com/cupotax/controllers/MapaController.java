package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/mapa")
@CrossOrigin(origins = "*")
public class MapaController {

    @Autowired
    private DatabaseReference database;

    @GetMapping("/vehiculos")
    public ResponseEntity<?> getVehiculosEnTiempoReal() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("vehiculos").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> vehiculos = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> v = new HashMap<>();
                        v.put("id", child.getKey());
                        if (child.getValue() != null) {
                            v.putAll((Map<String, Object>) child.getValue());
                        }
                        vehiculos.add(v);
                    }
                    future.complete(vehiculos);
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

    @PutMapping("/vehiculos/{vehiculoId}/ubicacion")
    public ResponseEntity<?> updateUbicacion(@PathVariable String vehiculoId, @RequestBody Map<String, Object> ubicacion) {
        try {
            ubicacion.put("ultimaActualizacion", new Date().toString());
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("vehiculos").child(vehiculoId).child("ubicacion").setValue(ubicacion, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Ubicación actualizada"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/rutas")
    public ResponseEntity<?> getRutas() {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("rutas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Map<String, Object>> rutas = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Map<String, Object> r = new HashMap<>();
                        r.put("id", child.getKey());
                        if (child.getValue() != null) {
                            r.putAll((Map<String, Object>) child.getValue());
                        }
                        rutas.add(r);
                    }
                    future.complete(rutas);
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

    @GetMapping("/demanda")
    public ResponseEntity<?> getDemandaTiempoReal() {
        try {
            // Simular demanda en tiempo real
            Map<String, Object> demanda = new HashMap<>();
            demanda.put("alta", new Random().nextInt(20) + 5);
            demanda.put("media", new Random().nextInt(30) + 10);
            demanda.put("baja", new Random().nextInt(15) + 3);
            demanda.put("actualizacion", new Date().toString());
            
            // Guardar en Firebase
            database.child("demanda").setValueAsync(demanda);
            
            return ResponseEntity.ok(demanda);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}