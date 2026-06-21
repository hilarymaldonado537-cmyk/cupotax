package com.cupotax.repositories;

import com.google.firebase.database.*;
import com.cupotax.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Repository
public class UserRepository {

    @Autowired
    private DatabaseReference database;
    private static final String COLLECTION = "usuarios";

    // ========== GUARDAR ==========
    public CompletableFuture<String> save(User user) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String uid = user.getUid();
        if (uid == null || uid.isEmpty()) {
            uid = database.child(COLLECTION).push().getKey();
            user.setUid(uid);
        }
        final String finalUid = uid;
        database.child(COLLECTION).child(uid).setValue(user, (error, ref) -> {
            if (error != null) {
                future.completeExceptionally(error.toException());
            } else {
                future.complete(finalUid);
            }
        });
        return future;
    }

    // ========== BUSCAR POR UID ==========
    public CompletableFuture<User> findById(String uid) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.child(COLLECTION).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.setUid(snapshot.getKey());
                }
                future.complete(user);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== BUSCAR POR EMAIL ==========
    public CompletableFuture<User> findByEmail(String email) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        break;
                    }
                }
                future.complete(user);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== BUSCAR POR USERNAME ==========
    public CompletableFuture<User> findByUsername(String username) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        break;
                    }
                }
                future.complete(user);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== BUSCAR POR TELÉFONO ==========
    public CompletableFuture<User> findByTelefono(String telefono) {
        CompletableFuture<User> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("telefono").equalTo(telefono).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        break;
                    }
                }
                future.complete(user);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== BUSCAR POR ROL ==========
    public CompletableFuture<List<User>> findByRole(String role) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("rol").equalTo(role).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        users.add(user);
                    }
                }
                future.complete(users);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== BUSCAR POR PRESTATARIA ==========
    public CompletableFuture<List<User>> findByPrestataria(String prestataria) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("prestataria").equalTo(prestataria).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        users.add(user);
                    }
                }
                future.complete(users);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== BUSCAR POR TIPO BUSERO ==========
    public CompletableFuture<List<User>> findByTipoBusero(String tipoBusero) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("tipoBusero").equalTo(tipoBusero).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        users.add(user);
                    }
                }
                future.complete(users);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== OBTENER TODOS ==========
    public CompletableFuture<List<User>> findAll() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        database.child(COLLECTION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        user.setUid(child.getKey());
                        users.add(user);
                    }
                }
                future.complete(users);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== ACTUALIZAR ==========
    public CompletableFuture<Void> update(String uid, Map<String, Object> updates) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        database.child(COLLECTION).child(uid).updateChildren(updates, (error, ref) -> {
            if (error != null) {
                future.completeExceptionally(error.toException());
            } else {
                future.complete(null);
            }
        });
        return future;
    }

    // ========== ELIMINAR ==========
    public CompletableFuture<Void> delete(String uid) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        database.child(COLLECTION).child(uid).removeValue((error, ref) -> {
            if (error != null) {
                future.completeExceptionally(error.toException());
            } else {
                future.complete(null);
            }
        });
        return future;
    }

    // ========== VERIFICAR SI EXISTE POR EMAIL ==========
    public CompletableFuture<Boolean> existsByEmail(String email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== VERIFICAR SI EXISTE POR USERNAME ==========
    public CompletableFuture<Boolean> existsByUsername(String username) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    // ========== VERIFICAR SI EXISTE POR TELÉFONO ==========
    public CompletableFuture<Boolean> existsByTelefono(String telefono) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        database.child(COLLECTION).orderByChild("telefono").equalTo(telefono).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
}