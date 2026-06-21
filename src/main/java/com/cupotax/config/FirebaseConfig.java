package com.cupotax.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.database-url}")
    private String databaseUrl;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            // 1. Intentar leer desde variable de entorno RENDER
            String credentialsJson = System.getenv("FIREBASE_CREDENTIALS");
            
            // 2. Si no hay variable de entorno, intentar leer archivo local
            if (credentialsJson == null || credentialsJson.isEmpty()) {
                System.err.println("⚠️ No se encontró variable FIREBASE_CREDENTIALS, usando archivo local...");
                // Usar el archivo local (solo para desarrollo)
                com.google.auth.oauth2.GoogleCredentials credentials = 
                    com.google.auth.oauth2.GoogleCredentials.fromStream(
                        new org.springframework.core.io.ClassPathResource("firebase-service-account.json").getInputStream()
                    );
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setDatabaseUrl(databaseUrl)
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("✅ Firebase initialized with local file!");
                }
                return;
            }

            // 3. Usar credenciales desde variable de entorno (RENDER)
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                        new ByteArrayInputStream(credentialsJson.getBytes())))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized with environment variable!");
            }

        } catch (Exception e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public DatabaseReference firebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}