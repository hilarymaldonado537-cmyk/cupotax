package com.cupotax.repositories;

import com.google.firebase.database.*;
import com.cupotax.models.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Repository
public class AuditLogRepository {

    @Autowired
    private DatabaseReference database;
    private static final String COLLECTION = "audit_logs";

    public CompletableFuture<String> save(AuditLog log) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String id = database.child(COLLECTION).push().getKey();
        log.setId(id);
        database.child(COLLECTION).child(id).setValue(log, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(id);
        });
        return future;
    }

    public CompletableFuture<List<AuditLog>> findByUsuarioId(String usuarioId) {
        CompletableFuture<List<AuditLog>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("usuarioId").equalTo(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<AuditLog> logs = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    AuditLog log = child.getValue(AuditLog.class);
                    if (log != null) { log.setId(child.getKey()); logs.add(log); }
                }
                future.complete(logs);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}