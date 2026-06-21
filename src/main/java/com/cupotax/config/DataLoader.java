package com.cupotax.config;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    public void run(String... args) {
        System.out.println("📦 Inicializando datos...");
        
        // Verificar si ya hay datos
        database.child("usuarios").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    System.out.println("✅ Datos ya existen en Firebase");
                    return;
                }

                System.out.println("📝 Creando datos de prueba...");
                crearDatosPrueba();
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                System.err.println("❌ Error: " + error.getMessage());
            }
        });
    }

    private void crearDatosPrueba() {
        // Admin
        Map<String, Object> admin = new HashMap<>();
        admin.put("nombre", "Administrador");
        admin.put("email", "admin@cupotax.com");
        admin.put("rol", "ADMIN");
        admin.put("estado", "Activo");
        admin.put("fechaRegistro", new java.util.Date().toString());
        database.child("usuarios").child("admin-uid").setValueAsync(admin);

        // Taxista
        Map<String, Object> taxista = new HashMap<>();
        taxista.put("nombre", "Taxista Test");
        taxista.put("email", "taxista@cupotax.com");
        taxista.put("rol", "TAXISTA");
        taxista.put("estado", "Disponible");
        Map<String, Object> vehiculo = new HashMap<>();
        vehiculo.put("placa", "T-123");
        vehiculo.put("modelo", "Toyota Hiace");
        vehiculo.put("capacidad", 4);
        taxista.put("vehiculo", vehiculo);
        database.child("usuarios").child("taxista-uid").setValueAsync(taxista);

        // Busero
        Map<String, Object> busero = new HashMap<>();
        busero.put("nombre", "Busero Test");
        busero.put("email", "busero@cupotax.com");
        busero.put("rol", "BUSERO");
        busero.put("estado", "En ruta");
        Map<String, Object> bus = new HashMap<>();
        bus.put("placa", "B-456");
        bus.put("modelo", "Mascarello");
        bus.put("capacidad", 40);
        bus.put("ruta", "Changuinola - Almirante");
        busero.put("vehiculo", bus);
        database.child("usuarios").child("busero-uid").setValueAsync(busero);

        // Usuario normal
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", "Usuario Test");
        usuario.put("email", "usuario@cupotax.com");
        usuario.put("rol", "USUARIO");
        usuario.put("estado", "Activo");
        database.child("usuarios").child("usuario-uid").setValueAsync(usuario);

        System.out.println("✅ Datos de prueba creados en Firebase");
    }
}