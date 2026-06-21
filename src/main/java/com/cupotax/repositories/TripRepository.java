package com.cupotax.repositories;

import com.google.firebase.database.*;
import com.cupotax.models.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Repository
public class TripRepository {

    @Autowired
    private DatabaseReference database;
    private static final String COLLECTION = "viajes";

    public CompletableFuture<String> save(Trip trip) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String id = database.child(COLLECTION).push().getKey();
        trip.setId(id);
        database.child(COLLECTION).child(id).setValue(trip, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(id);
        });
        return future;
    }

    public CompletableFuture<List<Trip>> findByUsuarioId(String usuarioId) {
        CompletableFuture<List<Trip>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("usuarioId").equalTo(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Trip> trips = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Trip trip = child.getValue(Trip.class);
                    if (trip != null) { 
                        trip.setId(child.getKey()); 
                        trips.add(trip); 
                    }
                }
                future.complete(trips);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}