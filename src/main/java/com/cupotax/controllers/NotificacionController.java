package com.cupotax.controllers;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private DatabaseReference database;

    @GetMapping("/user/{uid}")
    public ResponseEntity<?> getUserNotifications(@PathVariable String uid) {
        try {
            CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
            database.child("notificaciones").orderByChild("usuarioId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Map<String, Object> notification) {
        try {
            String notifId = database.child("notificaciones").push().getKey();
            notification.put("fecha", new Date().toString());
            notification.put("leida", false);
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("notificaciones").child(notifId).setValue(notification, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Notificación creada", "notificationId", notifId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{notifId}")
    public ResponseEntity<?> marcarLeida(@PathVariable String notifId) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("notificaciones").child(notifId).child("leida").setValue(true, (error, ref) -> {
                if (error != null) future.completeExceptionally(error.toException());
                else future.complete(null);
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Notificación marcada como leída"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/user/{uid}/marcar-todas")
    public ResponseEntity<?> marcarTodasLeidas(@PathVariable String uid) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            database.child("notificaciones").orderByChild("usuarioId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        child.getRef().child("leida").setValueAsync(true);
                    }
                    future.complete(null);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            future.get();
            return ResponseEntity.ok(Map.of("message", "Todas las notificaciones marcadas como leídas"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}