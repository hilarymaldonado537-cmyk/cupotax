package com.cupotax.repositories;

import com.google.firebase.database.*;
import com.cupotax.models.Incident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Repository
public class IncidentRepository {

    @Autowired
    private DatabaseReference database;
    private static final String COLLECTION = "incidentes";

    public CompletableFuture<String> save(Incident incident) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String id = database.child(COLLECTION).push().getKey();
        incident.setId(id);
        database.child(COLLECTION).child(id).setValue(incident, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(id);
        });
        return future;
    }

    public CompletableFuture<List<Incident>> findByUsuarioId(String usuarioId) {
        CompletableFuture<List<Incident>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("usuarioId").equalTo(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Incident> incidents = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Incident i = child.getValue(Incident.class);
                    if (i != null) { 
                        i.setId(child.getKey()); 
                        incidents.add(i); 
                    }
                }
                future.complete(incidents);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}