package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private DatabaseReference database;

    @GetMapping("/{uid}")
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

    @PutMapping("/{uid}")
    public ResponseEntity<?> updateUser(@PathVariable String uid, @RequestBody Map<String, Object> updates) {
        try {
            updates.put("fechaActualizacion", new Date().toString());
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("usuarios").child(uid).updateChildren(updates, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> userData) {
        try {
            String uid = (String) userData.get("uid");
            if (uid == null) {
                uid = database.child("usuarios").push().getKey();
            }
            
            userData.put("fechaRegistro", new Date().toString());
            userData.put("estado", "Activo");
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("usuarios").child(uid).setValue(userData, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Usuario creado", "uid", uid));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<?> deleteUser(@PathVariable String uid) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("usuarios").child(uid).removeValue((error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
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

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("usuarios").orderByChild("rol").equalTo(role).addListenerForSingleValueEvent(new ValueEventListener() {
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
}