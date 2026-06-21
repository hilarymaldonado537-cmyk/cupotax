package com.cupotax.repositories;

import com.google.firebase.database.*;
import com.cupotax.models.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Repository
public class NotificacionRepository {

    @Autowired
    private DatabaseReference database;
    private static final String COLLECTION = "notificaciones";

    public CompletableFuture<String> save(Notificacion notificacion) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String id = database.child(COLLECTION).push().getKey();
        notificacion.setId(id);
        database.child(COLLECTION).child(id).setValue(notificacion, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(id);
        });
        return future;
    }

    public CompletableFuture<List<Notificacion>> findByUsuarioId(String usuarioId) {
        CompletableFuture<List<Notificacion>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("usuarioId").equalTo(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notificacion> notifs = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notificacion n = child.getValue(Notificacion.class);
                    if (n != null) { 
                        n.setId(child.getKey()); 
                        notifs.add(n); 
                    }
                }
                future.complete(notifs);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}