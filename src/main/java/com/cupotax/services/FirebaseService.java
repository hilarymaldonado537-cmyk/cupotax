package com.cupotax.services;

import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseService {

    @Autowired
    private DatabaseReference databaseReference;

    public CompletableFuture<Void> saveData(String path, Object data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        databaseReference.child(path).setValue(data, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(null);
        });
        return future;
    }

    public CompletableFuture<Map<String, Object>> getData(String path) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> data = new HashMap<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    data.put(child.getKey(), child.getValue());
                }
                future.complete(data);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public CompletableFuture<Void> pushData(String path, Object data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        databaseReference.child(path).push().setValue(data, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(null);
        });
        return future;
    }

    public String generateKey(String path) {
        return databaseReference.child(path).push().getKey();
    }

    public DatabaseReference getReference(String path) {
        return databaseReference.child(path);
    }
}