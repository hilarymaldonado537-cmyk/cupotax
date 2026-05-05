package com.cupotax.controllers;

import com.cupotax.models.AuditLog;
import com.cupotax.models.Incident;
import com.cupotax.models.User;
import com.cupotax.repositories.AuditLogRepository;
import com.cupotax.repositories.IncidentRepository;
import com.cupotax.repositories.UserRepository;
import com.cupotax.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Rate limiting
    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Map<String, Long> lockoutTime = new HashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 15 * 60 * 1000;
    
    // Auditoria
    private void registrarAuditoria(String usuario, String accion, String ip, String detalles) {
        AuditLog log = new AuditLog();
        log.setUsuario(usuario);
        log.setAccion(accion);
        log.setIp(ip);
        log.setDetalles(detalles);
        auditLogRepository.save(log);
    }
    
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
        return telefono != null && telefono.matches("^[+]?[0-9]{7,15}$");
    }
    
    private boolean isValidCedula(String cedula) {
        return cedula != null && cedula.matches("^[0-9]{3,15}$");
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
        String ip = request.getRemoteAddr();
        
        if (!isValidEmail(email)) {
            response.put("error", "Formato de email invalido");
            return response;
        }
        
        if (!isValidPhone(telefono)) {
            response.put("error", "Formato de telefono invalido");
            return response;
        }
        
        if (!isValidCedula(cedula)) {
            response.put("error", "Formato de cedula invalido");
            return response;
        }
        
        if (!pinSeguridad.matches("\\d{6}")) {
            response.put("error", "El PIN debe ser de 6 digitos");
            return response;
        }
        
        List<String> validRoles = Arrays.asList("ADMIN", "TAXISTA", "BUSERO", "USUARIO", "ESTUDIANTE");
        if (!validRoles.contains(rol)) {
            response.put("error", "Rol invalido");
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
            return response;
        }
        
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("error", "El correo ya esta registrado");
            return response;
        }
        
        String passwordError = validarPasswordFuerte(password);
        if (passwordError != null) {
            response.put("error", passwordError);
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
                    return response;
                }
                user.setFotoBus(fotoVehiculo.getBytes());
            }
        }
        
        userRepository.save(user);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(username, rol);
        
        // Registrar auditoria
        registrarAuditoria(username, "REGISTRO", ip, "Nuevo usuario registrado con rol: " + rol);
        
        response.put("success", true);
        response.put("redirectUrl", getRedirectUrl(rol));
        response.put("nombre", user.getNombreCompleto());
        response.put("token", token);
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
                registrarAuditoria(username, "LOGIN_FALLIDO", ip, "Cuenta bloqueada");
                response.put("success", false);
                response.put("error", "Tu cuenta esta bloqueada");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                loginAttempts.remove(ip);
                lockoutTime.remove(ip);
                
                // Generar token JWT
                String token = jwtUtil.generateToken(user.getUsername(), user.getRol());
                
                // Registrar auditoria
                registrarAuditoria(user.getUsername(), "LOGIN_EXITOSO", ip, "Inicio de sesion exitoso");
                
                response.put("success", true);
                response.put("rol", user.getRol());
                response.put("redirectUrl", getRedirectUrl(user.getRol()));
                response.put("nombre", user.getNombreCompleto());
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }
        
        int attempts = loginAttempts.getOrDefault(ip, 0) + 1;
        loginAttempts.put(ip, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            lockoutTime.put(ip, System.currentTimeMillis() + LOCKOUT_DURATION);
            loginAttempts.remove(ip);
            registrarAuditoria(username, "BLOQUEO_IP", ip, "IP bloqueada por intentos fallidos");
            response.put("error", "Demasiados intentos. Cuenta bloqueada 15 minutos");
        } else {
            registrarAuditoria(username, "LOGIN_FALLIDO", ip, "Intento de login fallido");
            response.put("error", "Usuario o contrasena incorrectos. Intentos restantes: " + (MAX_ATTEMPTS - attempts));
        }
        
        response.put("success", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        registrarAuditoria("SISTEMA", "LOGOUT", ip, "Cierre de sesion");
        
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
    
    @PostMapping("/api/incidentes/reportar")
    public ResponseEntity<Map<String, Object>> reportarIncidente(@RequestBody Map<String, Object> request, 
                                                                  HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        String ip = httpRequest.getRemoteAddr();
        
        String tipo = (String) request.get("tipo");
        String descripcion = (String) request.get("descripcion");
        String ubicacion = (String) request.get("ubicacion");
        String contacto = (String) request.get("contacto");
        String username = (String) request.get("username");
        
        tipo = sanitizar(tipo);
        descripcion = sanitizar(descripcion);
        ubicacion = sanitizar(ubicacion);
        contacto = sanitizar(contacto);
        
        Incident incident = new Incident();
        incident.setTipo(tipo);
        incident.setDescripcion(descripcion);
        incident.setUbicacion(ubicacion);
        incident.setContacto(contacto);
        incident.setAtendido(false);
        incidentRepository.save(incident);
        
        registrarAuditoria(username != null ? username : "ANONIMO", "SOS_ENVIADO", ip, "Tipo: " + tipo);
        
        response.put("success", true);
        response.put("message", "Alerta SOS enviada correctamente");
        return ResponseEntity.ok(response);
    }
    
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
    public String debugUsers(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        // Solo permitir acceso local o especifico
        if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) {
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
        sb.append("<td>");
        sb.append("氢<th>ID</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Activo</th><th>Telefono</th>");
        
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