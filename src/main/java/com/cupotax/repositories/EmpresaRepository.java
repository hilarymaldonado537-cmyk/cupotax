package com.cupotax.repositories;

import com.google.firebase.database.*;
import com.cupotax.models.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Repository
public class EmpresaRepository {

    @Autowired
    private DatabaseReference database;
    private static final String COLLECTION = "empresas";

    public CompletableFuture<String> save(Empresa empresa) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String id = database.child(COLLECTION).push().getKey();
        empresa.setId(id);
        database.child(COLLECTION).child(id).setValue(empresa, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(id);
        });
        return future;
    }

    public CompletableFuture<List<Empresa>> findAll() {
        CompletableFuture<List<Empresa>> future = new CompletableFuture<>();
        database.child(COLLECTION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Empresa> empresas = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Empresa e = child.getValue(Empresa.class);
                    if (e != null) { e.setId(child.getKey()); empresas.add(e); }
                }
                future.complete(empresas);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}