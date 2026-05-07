package com.cupotax.controllers;

import com.cupotax.models.Incident;
import com.cupotax.models.User;
import com.cupotax.repositories.IncidentRepository;
import com.cupotax.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@RestController
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IncidentRepository incidentRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Rate limiting
    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Map<String, Long> lockoutTime = new HashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 15 * 60 * 1000;
    
    // Sanitizacion
    private String sanitizar(String input) {
        if (input == null) return null;
        return input.replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#x27;")
                    .replaceAll("/", "&#x2F;");
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return email != null && pat.matcher(email).matches();
    }
    
    private boolean isValidPhone(String telefono) {
        return telefono != null && telefono.matches("^[+]?[0-9\\-]{7,20}$");
    }
    
    private boolean isValidCedula(String cedula) {
        return cedula != null && cedula.matches("^[0-9\\-]{6,15}$");
    }
    
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(
            @RequestParam String nombreCompleto,
            @RequestParam String cedula,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String rol,
            @RequestParam String pinSeguridad,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) String numeroVehiculo,
            @RequestParam(required = false) String tipoTaxi,
            @RequestParam(required = false) String rutaBusero,
            @RequestParam(required = false) String rutaSeleccionada,
            @RequestParam(required = false) String chasis,
            @RequestParam(required = false) String fechaMatricula,
            @RequestParam(required = false) String ultimaRevision,
            @RequestParam(required = false) String proximaRevision,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            @RequestParam(required = false) MultipartFile fotoVehiculo,
            HttpServletRequest request) throws IOException {
        
        Map<String, Object> response = new HashMap<>();
        
        if (!isValidEmail(email)) {
            response.put("error", "Formato de email invalido");
            response.put("success", false);
            return response;
        }
        
        if (!isValidPhone(telefono)) {
            response.put("error", "Formato de telefono invalido. Ej: 6663-7549");
            response.put("success", false);
            return response;
        }
        
        if (!isValidCedula(cedula)) {
            response.put("error", "Formato de cedula invalido. Ej: 1-761-1069");
            response.put("success", false);
            return response;
        }
        
        if (!pinSeguridad.matches("\\d{6}")) {
            response.put("error", "El PIN debe ser de 6 digitos");
            response.put("success", false);
            return response;
        }
        
        List<String> validRoles = Arrays.asList("ADMIN", "TAXISTA", "BUSERO", "USUARIO", "ESTUDIANTE");
        if (!validRoles.contains(rol)) {
            response.put("error", "Rol invalido");
            response.put("success", false);
            return response;
        }
        
        nombreCompleto = sanitizar(nombreCompleto);
        cedula = sanitizar(cedula);
        email = sanitizar(email);
        telefono = sanitizar(telefono);
        username = sanitizar(username);
        placa = sanitizar(placa);
        modelo = sanitizar(modelo);
        color = sanitizar(color);
        numeroVehiculo = sanitizar(numeroVehiculo);
        tipoTaxi = sanitizar(tipoTaxi);
        rutaBusero = sanitizar(rutaBusero);
        rutaSeleccionada = sanitizar(rutaSeleccionada);
        chasis = sanitizar(chasis);
        
        if (userRepository.findByUsername(username).isPresent()) {
            response.put("error", "El nombre de usuario ya existe");
            response.put("success", false);
            return response;
        }
        
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("error", "El correo ya esta registrado");
            response.put("success", false);
            return response;
        }
        
        String passwordError = validarPasswordFuerte(password);
        if (passwordError != null) {
            response.put("error", passwordError);
            response.put("success", false);
            return response;
        }
        
        User user = new User();
        user.setNombreCompleto(nombreCompleto);
        user.setCedula(cedula);
        user.setEmail(email);
        user.setTelefono(telefono);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(rol);
        user.setActivo(true);
        user.setPinSeguridad(pinSeguridad);
        
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            if (fotoPerfil.getSize() > 5 * 1024 * 1024) {
                response.put("error", "La foto no puede superar los 5MB");
                response.put("success", false);
                return response;
            }
            user.setFotoPerfil(fotoPerfil.getBytes());
        }
        
        if ("TAXISTA".equals(rol) || "BUSERO".equals(rol)) {
            user.setPlaca(placa);
            user.setModelo(modelo);
            user.setColor(color);
            user.setCapacidad(capacidad != null ? capacidad : ("TAXISTA".equals(rol) ? 4 : 50));
            user.setTipoTaxi(tipoTaxi);
            user.setRutaBusero(rutaBusero);
            user.setRutaSeleccionada(rutaSeleccionada);
            user.setAnioVehiculo(anio);
            user.setNumeroBus(numeroVehiculo);
            user.setChasis(chasis);
            
            if (fechaMatricula != null && !fechaMatricula.isEmpty()) {
                try {
                    user.setFechaMatricula(LocalDateTime.parse(fechaMatricula + "T00:00:00"));
                } catch (Exception e) {}
            }
            if (ultimaRevision != null && !ultimaRevision.isEmpty()) {
                try {
                    user.setUltimaRevision(LocalDateTime.parse(ultimaRevision + "T00:00:00"));
                } catch (Exception e) {}
            }
            if (proximaRevision != null && !proximaRevision.isEmpty()) {
                try {
                    user.setProximaRevision(LocalDateTime.parse(proximaRevision + "T00:00:00"));
                } catch (Exception e) {}
            }
            
            if (fotoVehiculo != null && !fotoVehiculo.isEmpty()) {
                if (fotoVehiculo.getSize() > 5 * 1024 * 1024) {
                    response.put("error", "La foto del vehiculo no puede superar los 5MB");
                    response.put("success", false);
                    return response;
                }
                user.setFotoBus(fotoVehiculo.getBytes());
            }
        }
        
        userRepository.save(user);
        
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("userId", user.getId());
        newSession.setAttribute("username", user.getUsername());
        newSession.setAttribute("rol", user.getRol());
        newSession.setAttribute("nombre", user.getNombreCompleto());
        
        response.put("success", true);
        response.put("redirectUrl", getRedirectUrl(rol));
        response.put("nombre", user.getNombreCompleto());
        response.put("rol", user.getRol());
        return response;
    }
    
    private String validarPasswordFuerte(String password) {
        if (password == null || password.length() < 8) {
            return "La contrasena debe tener al menos 8 caracteres";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "La contrasena debe tener al menos una MAYUSCULA";
        }
        if (!password.matches(".*[a-z].*")) {
            return "La contrasena debe tener al menos una MINUSCULA";
        }
        if (!password.matches(".*[0-9].*")) {
            return "La contrasena debe tener al menos un NUMERO";
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            return "La contrasena debe tener al menos un CARACTER ESPECIAL";
        }
        return null;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username,
                                                      @RequestParam String password,
                                                      HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        String ip = request.getRemoteAddr();
        
        if (lockoutTime.containsKey(ip) && System.currentTimeMillis() < lockoutTime.get(ip)) {
            long remaining = (lockoutTime.get(ip) - System.currentTimeMillis()) / 1000;
            response.put("success", false);
            response.put("error", "Demasiados intentos. Intenta en " + remaining + " segundos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        username = sanitizar(username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (!user.isActivo()) {
                response.put("success", false);
                response.put("error", "Tu cuenta esta bloqueada");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                loginAttempts.remove(ip);
                lockoutTime.remove(ip);
                
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession newSession = request.getSession(true);
                
                newSession.setAttribute("userId", user.getId());
                newSession.setAttribute("username", user.getUsername());
                newSession.setAttribute("rol", user.getRol());
                newSession.setAttribute("nombre", user.getNombreCompleto());
                newSession.setAttribute("email", user.getEmail());
                
                response.put("success", true);
                response.put("rol", user.getRol());
                response.put("redirectUrl", getRedirectUrl(user.getRol()));
                return ResponseEntity.ok(response);
            }
        }
        
        int attempts = loginAttempts.getOrDefault(ip, 0) + 1;
        loginAttempts.put(ip, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            lockoutTime.put(ip, System.currentTimeMillis() + LOCKOUT_DURATION);
            loginAttempts.remove(ip);
            response.put("error", "Demasiados intentos. Cuenta bloqueada 15 minutos");
        } else {
            response.put("error", "Usuario o contrasena incorrectos. Intentos restantes: " + (MAX_ATTEMPTS - attempts));
        }
        
        response.put("success", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @PostMapping("/login-with-pin")
    public ResponseEntity<Map<String, Object>> loginWithPin(@RequestParam String username,
                                                             @RequestParam String pin,
                                                             HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        String ip = request.getRemoteAddr();
        
        if (lockoutTime.containsKey(ip) && System.currentTimeMillis() < lockoutTime.get(ip)) {
            long remaining = (lockoutTime.get(ip) - System.currentTimeMillis()) / 1000;
            response.put("success", false);
            response.put("error", "Demasiados intentos. Intenta en " + remaining + " segundos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        username = sanitizar(username);
        pin = sanitizar(pin);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (!user.isActivo()) {
                response.put("success", false);
                response.put("error", "Tu cuenta esta bloqueada");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (user.getPinSeguridad() != null && user.getPinSeguridad().equals(pin)) {
                loginAttempts.remove(ip);
                lockoutTime.remove(ip);
                
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession newSession = request.getSession(true);
                
                newSession.setAttribute("userId", user.getId());
                newSession.setAttribute("username", user.getUsername());
                newSession.setAttribute("rol", user.getRol());
                newSession.setAttribute("nombre", user.getNombreCompleto());
                newSession.setAttribute("email", user.getEmail());
                
                response.put("success", true);
                response.put("rol", user.getRol());
                response.put("redirectUrl", getRedirectUrl(user.getRol()));
                return ResponseEntity.ok(response);
            }
        }
        
        int attempts = loginAttempts.getOrDefault(ip, 0) + 1;
        loginAttempts.put(ip, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            lockoutTime.put(ip, System.currentTimeMillis() + LOCKOUT_DURATION);
            loginAttempts.remove(ip);
            response.put("error", "Demasiados intentos. Cuenta bloqueada 15 minutos");
        } else {
            response.put("error", "PIN incorrecto. Intentos restantes: " + (MAX_ATTEMPTS - attempts));
        }
        
        response.put("success", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @PostMapping("/recuperar/enviar-codigo")
    public ResponseEntity<Map<String, Object>> enviarCodigoRecuperacion(@RequestParam String contacto) {
        Map<String, Object> response = new HashMap<>();
        
        contacto = sanitizar(contacto);
        
        Optional<User> userOpt = userRepository.findByEmail(contacto);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByTelefono(contacto);
        }
        
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "No se encontro una cuenta");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        String codigo = String.format("%06d", new Random().nextInt(999999));
        String expiracion = LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        user.setCodigoRecuperacion(codigo);
        user.setCodigoRecuperacionExpiracion(expiracion);
        userRepository.save(user);
        
        System.out.println("CODIGO DE RECUPERACION: " + codigo);
        
        response.put("success", true);
        response.put("message", "Codigo enviado");
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/recuperar/verificar-codigo")
    public ResponseEntity<Map<String, Object>> verificarCodigo(@RequestParam String email,
                                                                @RequestParam String codigo) {
        Map<String, Object> response = new HashMap<>();
        
        email = sanitizar(email);
        codigo = sanitizar(codigo);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        
        if (user.getCodigoRecuperacion() == null || !user.getCodigoRecuperacion().equals(codigo)) {
            response.put("success", false);
            response.put("error", "Codigo incorrecto");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (user.getCodigoRecuperacionExpiracion() != null) {
            LocalDateTime expiracion = LocalDateTime.parse(user.getCodigoRecuperacionExpiracion());
            if (expiracion.isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("error", "Codigo expirado");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        response.put("success", true);
        response.put("message", "Codigo verificado");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/recuperar/cambiar-password")
    public ResponseEntity<Map<String, Object>> cambiarPassword(@RequestParam String email,
                                                                @RequestParam String nuevaPassword) {
        Map<String, Object> response = new HashMap<>();
        
        email = sanitizar(email);
        
        String passwordError = validarPasswordFuerte(nuevaPassword);
        if (passwordError != null) {
            response.put("success", false);
            response.put("error", passwordError);
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        user.setCodigoRecuperacion(null);
        user.setCodigoRecuperacionExpiracion(null);
        userRepository.save(user);
        
        response.put("success", true);
        response.put("message", "Contrasena actualizada");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/perfil")
    public ResponseEntity<Map<String, Object>> getPerfil(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("nombreCompleto", user.getNombreCompleto());
        response.put("email", user.getEmail());
        response.put("telefono", user.getTelefono());
        response.put("rol", user.getRol());
        
        if (user.getFotoPerfil() != null) {
            response.put("fotoPerfil", Base64.getEncoder().encodeToString(user.getFotoPerfil()));
        }
        
        // ========== AGREGAR FOTO DEL VEHÍCULO ==========
        if (user.getFotoBus() != null) {
            response.put("fotoVehiculo", Base64.getEncoder().encodeToString(user.getFotoBus()));
        }
        // ==============================================
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/perfil/actualizar")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(@RequestParam String nombreCompleto,
                                                                  @RequestParam String telefono,
                                                                  @RequestParam(required = false) MultipartFile fotoPerfil,
                                                                  HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (!isValidPhone(telefono)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Formato de telefono invalido");
            return ResponseEntity.badRequest().body(response);
        }
        
        nombreCompleto = sanitizar(nombreCompleto);
        telefono = sanitizar(telefono);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        user.setNombreCompleto(nombreCompleto);
        user.setTelefono(telefono);
        
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            if (fotoPerfil.getSize() > 5 * 1024 * 1024) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "La foto no puede superar los 5MB");
                return ResponseEntity.badRequest().body(response);
            }
            user.setFotoPerfil(fotoPerfil.getBytes());
            session.setAttribute("fotoPerfil", Base64.getEncoder().encodeToString(fotoPerfil.getBytes()));
        }
        
        userRepository.save(user);
        session.setAttribute("nombre", user.getNombreCompleto());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/cambiar-password")
    public ResponseEntity<Map<String, Object>> cambiarPasswordPerfil(@RequestParam String actualPassword,
                                                                       @RequestParam String nuevaPassword,
                                                                       HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(actualPassword, user.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Contrasena actual incorrecta");
            return ResponseEntity.badRequest().body(response);
        }
        
        String passwordError = validarPasswordFuerte(nuevaPassword);
        if (passwordError != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", passwordError);
            return ResponseEntity.badRequest().body(response);
        }
        
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Contrasena actualizada");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
    
    private String getRedirectUrl(String rol) {
        switch (rol) {
            case "ADMIN": return "/admin";
            case "TAXISTA": return "/taxista";
            case "BUSERO": return "/busero";
            default: return "/usuario";
        }
    }
    
    // ========== MÉTODO REPORTAR INCIDENTE CON FOTO (CORREGIDO) ==========
    @PostMapping("/api/incidentes/reportar")
    public ResponseEntity<Map<String, Object>> reportarIncidente(
            @RequestParam("tipo") String tipo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("ubicacion") String ubicacion,
            @RequestParam("contacto") String contacto,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            HttpSession session) throws IOException {
        
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            response.put("error", "Usuario no autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        tipo = sanitizar(tipo);
        descripcion = sanitizar(descripcion);
        ubicacion = sanitizar(ubicacion);
        contacto = sanitizar(contacto);
        
        Incident incident = new Incident();
        incident.setUsuario(userOpt.get());
        incident.setTipo(tipo);
        incident.setDescripcion(descripcion);
        incident.setUbicacion(ubicacion);
        incident.setContacto(contacto);
        incident.setAtendido(false);
        incident.setFecha(LocalDateTime.now());
        
        // Guardar foto si viene
        if (foto != null && !foto.isEmpty()) {
            if (foto.getSize() > 5 * 1024 * 1024) {
                response.put("error", "La foto no puede superar los 5MB");
                return ResponseEntity.badRequest().body(response);
            }
            incident.setFotoIncidente(foto.getBytes());
        }
        
        incidentRepository.save(incident);
        
        response.put("success", true);
        response.put("message", "Alerta SOS enviada correctamente");
        return ResponseEntity.ok(response);
    }
    // =====================================================================
    
    @GetMapping("/api/incidentes/no-atendidos")
    public ResponseEntity<List<Incident>> getIncidentesNoAtendidos() {
        return ResponseEntity.ok(incidentRepository.findByAtendidoOrderByFechaDesc(false));
    }
    
    @GetMapping("/api/incidentes/todos")
    public ResponseEntity<List<Incident>> getTodosIncidentes() {
        return ResponseEntity.ok(incidentRepository.findAllByOrderByFechaDesc());
    }
    
    @PostMapping("/api/incidentes/atender/{id}")
    public ResponseEntity<?> atenderIncidente(@PathVariable Long id) {
        incidentRepository.marcarComoAtendido(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/debug/users")
    public String debugUsers(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("rol"))) {
            return "Acceso denegado";
        }
        
        List<User> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Usuarios CupoTax</title><style>");
        sb.append("body{background:#0a0a0a;color:#fff;font-family:Arial;padding:20px;}");
        sb.append("table{border-collapse:collapse;width:100%;}");
        sb.append("th,td{border:1px solid #FFD700;padding:8px;text-align:left;}");
        sb.append("th{background:#FFD700;color:#000;}");
        sb.append("</style></head><body>");
        sb.append("<h1 style='color:#FFD700;'>Usuarios registrados</h1>");
        sb.append("<p>Total: <strong>").append(users.size()).append("</strong> usuarios</p>");
        sb.append("<table>");
        sb.append("<tr><th>ID</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Activo</th><th>Telefono</th></tr>");
        
        for (User u : users) {
            sb.append("<tr>");
            sb.append("<td>").append(u.getId()).append("</td>");
            sb.append("<td>").append(u.getNombreCompleto() != null ? u.getNombreCompleto() : u.getUsername()).append("</td>");
            sb.append("<td>").append(u.getEmail()).append("</td>");
            sb.append("<td>").append(u.getRol()).append("</td>");
            sb.append("<td style='color:").append(u.isActivo() ? "#2ecc71" : "#e74c3c").append("'>").append(u.isActivo() ? "Activo" : "Inactivo").append("</td>");
            sb.append("<td>").append(u.getTelefono() != null ? u.getTelefono() : "-").append("</td>");
            sb.append("</tr>");
        }
        
        sb.append("</table>");
        sb.append("<p><a href='/'>Volver a CupoTax</a></p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}