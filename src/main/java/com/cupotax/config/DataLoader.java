package com.cupotax.config;

// import com.cupotax.models.Notification;  // COMENTADO - Archivo borrado
import com.cupotax.models.Trip;
import com.cupotax.models.User;
// import com.cupotax.repositories.NotificationRepository;  // COMENTADO - Archivo borrado
import com.cupotax.repositories.TripRepository;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    // @Autowired  // COMENTADO - Temporalmente deshabilitado
    // private NotificationRepository notificationRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        
        // Solo cargar si no hay usuarios
        if (userRepository.count() == 0) {
            
            // 1. ADMIN
            User admin = new User();
            admin.setNombreCompleto("Administrador CupoTax");
            admin.setCedula("0-000-000");
            admin.setEmail("admin@cupotax.com");
            admin.setTelefono("6000-0000");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setPinSeguridad("123456");
            admin.setActivo(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            
            // 2. TAXISTA 1
            User taxista1 = new User();
            taxista1.setNombreCompleto("Juan Pérez");
            taxista1.setCedula("4-123-456");
            taxista1.setEmail("juan@cupotax.com");
            taxista1.setTelefono("6000-1111");
            taxista1.setUsername("juanperez");
            taxista1.setPassword(passwordEncoder.encode("123456"));
            taxista1.setRol("TAXISTA");
            taxista1.setPinSeguridad("111111");
            taxista1.setPlaca("AF6339");
            taxista1.setModelo("Mascarello");
            taxista1.setColor("Blanco");
            taxista1.setCapacidad(4);
            taxista1.setTipoTaxi("URBANO");
            taxista1.setActivo(true);
            taxista1.setCreatedAt(LocalDateTime.now());
            userRepository.save(taxista1);
            
            // 3. TAXISTA 2
            User taxista2 = new User();
            taxista2.setNombreCompleto("María Gómez");
            taxista2.setCedula("4-789-012");
            taxista2.setEmail("maria@cupotax.com");
            taxista2.setTelefono("6000-2222");
            taxista2.setUsername("mariagomez");
            taxista2.setPassword(passwordEncoder.encode("123456"));
            taxista2.setRol("TAXISTA");
            taxista2.setPinSeguridad("222222");
            taxista2.setPlaca("CD9012");
            taxista2.setModelo("Hyundai");
            taxista2.setColor("Negro");
            taxista2.setCapacidad(4);
            taxista2.setTipoTaxi("PREMIUM");
            taxista2.setActivo(true);
            taxista2.setCreatedAt(LocalDateTime.now());
            userRepository.save(taxista2);
            
            // 4. BUSERO
            User busero = new User();
            busero.setNombreCompleto("Carlos Rodríguez");
            busero.setCedula("4-345-678");
            busero.setEmail("carlos@cupotax.com");
            busero.setTelefono("6000-3333");
            busero.setUsername("carlosbus");
            busero.setPassword(passwordEncoder.encode("123456"));
            busero.setRol("BUSERO");
            busero.setPinSeguridad("333333");
            busero.setPlaca("BC7845");
            busero.setModelo("Diablo");
            busero.setColor("Azul");
            busero.setCapacidad(50);
            busero.setRutaBusero("Changuinola - Almirante");
            busero.setActivo(true);
            busero.setCreatedAt(LocalDateTime.now());
            userRepository.save(busero);
            
            // 5. USUARIO NORMAL
            User usuario = new User();
            usuario.setNombreCompleto("Ana Martínez");
            usuario.setCedula("4-456-789");
            usuario.setEmail("ana@mail.com");
            usuario.setTelefono("6000-4444");
            usuario.setUsername("anamartinez");
            usuario.setPassword(passwordEncoder.encode("123456"));
            usuario.setRol("USUARIO");
            usuario.setPinSeguridad("444444");
            usuario.setActivo(true);
            usuario.setCreatedAt(LocalDateTime.now());
            userRepository.save(usuario);
            
            // 6. ESTUDIANTE (para tarjeta digital)
            User estudiante = new User();
            estudiante.setNombreCompleto("Pedro Estudiante");
            estudiante.setCedula("4-567-890");
            estudiante.setEmail("estudiante@mail.com");
            estudiante.setTelefono("6000-5555");
            estudiante.setUsername("pedroestudiante");
            estudiante.setPassword(passwordEncoder.encode("123456"));
            estudiante.setRol("ESTUDIANTE");
            estudiante.setPinSeguridad("555555");
            estudiante.setActivo(true);
            estudiante.setCreatedAt(LocalDateTime.now());
            userRepository.save(estudiante);
            
            // 7. VIAJES DE EJEMPLO
            Trip trip1 = new Trip();
            trip1.setUsuario(usuario);
            trip1.setConductor(taxista1);
            trip1.setOrigen("Changuinola Centro");
            trip1.setDestino("Almirante");
            trip1.setTarifa(14.50);
            trip1.setTipoServicio("estandar");
            trip1.setTipoViaje("TAXI");
            trip1.setEstado("completado");
            trip1.setFechaSolicitud(LocalDateTime.now().minusDays(2));
            tripRepository.save(trip1);
            
            Trip trip2 = new Trip();
            trip2.setUsuario(usuario);
            trip2.setConductor(busero);
            trip2.setOrigen("Changuinola Centro");
            trip2.setDestino("David");
            trip2.setTarifa(9.65);
            trip2.setTipoServicio("premium");
            trip2.setTipoViaje("BUS");
            trip2.setEstado("completado");
            trip2.setFechaSolicitud(LocalDateTime.now().minusDays(1));
            tripRepository.save(trip2);
            
            Trip trip3 = new Trip();
            trip3.setUsuario(estudiante);
            trip3.setConductor(taxista2);
            trip3.setOrigen("Terminal");
            trip3.setDestino("Finca 60");
            trip3.setTarifa(6.05);
            trip3.setTipoServicio("estandar");
            trip3.setTipoViaje("TAXI");
            trip3.setEstado("pendiente");
            trip3.setFechaSolicitud(LocalDateTime.now());
            tripRepository.save(trip3);
            
            // ========== NOTIFICACIONES COMENTADAS TEMPORALMENTE ==========
            /*
            Notification notif1 = new Notification();
            notif1.setUsuario(usuario);
            notif1.setTitulo("Viaje completado");
            notif1.setMensaje("Tu viaje a Almirante ha sido completado exitosamente.");
            notificationRepository.save(notif1);
            
            Notification notif2 = new Notification();
            notif2.setUsuario(usuario);
            notif2.setTitulo("Bienvenido a CupoTax");
            notif2.setMensaje("Gracias por confiar en nosotros. Disfruta de tus viajes.");
            notificationRepository.save(notif2);
            
            Notification notif3 = new Notification();
            notif3.setUsuario(taxista1);
            notif3.setTitulo("Nueva solicitud de viaje");
            notif3.setMensaje("Tienes un nuevo viaje pendiente desde Changuinola Centro.");
            notificationRepository.save(notif3);
            */
            
            System.out.println("=========================================");
            System.out.println("✅ DATOS DE PRUEBA CARGADOS EN LA BASE DE DATOS");
            System.out.println("📋 Usuarios: ADMIN, 2 TAXISTAS, 1 BUSERO, 1 USUARIO, 1 ESTUDIANTE");
            System.out.println("📋 Viajes: 3");
            System.out.println("📋 Notificaciones: 0 (temporalmente deshabilitadas)");
            System.out.println("=========================================");
        }
    }
}