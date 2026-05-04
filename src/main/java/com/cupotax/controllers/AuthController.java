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

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IncidentRepository incidentRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
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
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) String tipoTaxi,
            @RequestParam(required = false) String rutaBusero,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            @RequestParam(required = false) MultipartFile fotoBus,
            HttpSession session) throws IOException {
        
        Map<String, Object> response = new HashMap<>();
        
        if (userRepository.findByUsername(username).isPresent()) {
            response.put("error", "El nombre de usuario ya existe");
            return response;
        }
        
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("error", "El correo ya está registrado");
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
            user.setFotoPerfil(fotoPerfil.getBytes());
        }
        
        if ("TAXISTA".equals(rol) || "BUSERO".equals(rol)) {
            user.setPlaca(placa);
            user.setModelo(modelo);
            user.setColor(color);
            user.setCapacidad(capacidad != null ? capacidad : ("TAXISTA".equals(rol) ? 4 : 50));
            user.setTipoTaxi(tipoTaxi);
            user.setRutaBusero(rutaBusero);
            
            if (fotoBus != null && !fotoBus.isEmpty()) {
                user.setFotoBus(fotoBus.getBytes());
            }
        }
        
        userRepository.save(user);
        
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("rol", user.getRol());
        session.setAttribute("nombre", user.getNombreCompleto());
        
        response.put("success", true);
        response.put("redirectUrl", getRedirectUrl(rol));
        response.put("nombre", user.getNombreCompleto());
        return response;
    }
    
    private String validarPasswordFuerte(String password) {
        if (password == null || password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe tener al menos una MAYÚSCULA";
        }
        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe tener al menos una MINÚSCULA";
        }
        if (!password.matches(".*[0-9].*")) {
            return "La contraseña debe tener al menos un NÚMERO";
        }
        return null;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username,
                                                      @RequestParam String password,
                                                      HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (!user.isActivo()) {
                response.put("success", false);
                response.put("error", "Tu cuenta está bloqueada");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("rol", user.getRol());
                session.setAttribute("nombre", user.getNombreCompleto());
                
                response.put("success", true);
                response.put("rol", user.getRol());
                response.put("redirectUrl", getRedirectUrl(user.getRol()));
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("success", false);
        response.put("error", "Usuario o contraseña incorrectos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @PostMapping("/login-with-pin")
    public ResponseEntity<Map<String, Object>> loginWithPin(@RequestParam String username,
                                                             @RequestParam String pin,
                                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (!user.isActivo()) {
                response.put("success", false);
                response.put("error", "Tu cuenta está bloqueada");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (user.getPinSeguridad() != null && user.getPinSeguridad().equals(pin)) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("rol", user.getRol());
                session.setAttribute("nombre", user.getNombreCompleto());
                
                response.put("success", true);
                response.put("rol", user.getRol());
                response.put("redirectUrl", getRedirectUrl(user.getRol()));
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("success", false);
        response.put("error", "PIN incorrecto");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @PostMapping("/recuperar/enviar-codigo")
    public ResponseEntity<Map<String, Object>> enviarCodigoRecuperacion(@RequestParam String contacto) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(contacto);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByTelefono(contacto);
        }
        
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "No se encontró una cuenta");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        String codigo = String.format("%06d", new Random().nextInt(999999));
        String expiracion = LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        user.setCodigoRecuperacion(codigo);
        user.setCodigoRecuperacionExpiracion(expiracion);
        userRepository.save(user);
        
        System.out.println("CÓDIGO DE RECUPERACIÓN: " + codigo);
        
        response.put("success", true);
        response.put("message", "Código enviado");
        response.put("email", user.getEmail());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/recuperar/verificar-codigo")
    public ResponseEntity<Map<String, Object>> verificarCodigo(@RequestParam String email,
                                                                @RequestParam String codigo) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("error", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        
        if (user.getCodigoRecuperacion() == null || !user.getCodigoRecuperacion().equals(codigo)) {
            response.put("success", false);
            response.put("error", "Código incorrecto");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (user.getCodigoRecuperacionExpiracion() != null) {
            LocalDateTime expiracion = LocalDateTime.parse(user.getCodigoRecuperacionExpiracion());
            if (expiracion.isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("error", "Código expirado");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        response.put("success", true);
        response.put("message", "Código verificado");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/recuperar/cambiar-password")
    public ResponseEntity<Map<String, Object>> cambiarPassword(@RequestParam String email,
                                                                @RequestParam String nuevaPassword) {
        Map<String, Object> response = new HashMap<>();
        
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
        response.put("message", "Contraseña actualizada");
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
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        user.setNombreCompleto(nombreCompleto);
        user.setTelefono(telefono);
        
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
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
            response.put("error", "Contraseña actual incorrecta");
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
        response.put("message", "Contraseña actualizada");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    private String getRedirectUrl(String rol) {
        switch (rol) {
            case "ADMIN": return "/admin";
            case "TAXISTA": return "/taxista";
            case "BUSERO": return "/busero";
            default: return "/usuario";
        }
    }
    
    // ==================== SISTEMA SOS ====================
    
    @PostMapping("/api/incidentes/reportar")
    public ResponseEntity<Map<String, Object>> reportarIncidente(@RequestBody Map<String, Object> request, HttpSession session) {
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
        
        Incident incident = new Incident();
        incident.setUsuario(userOpt.get());
        incident.setTipo((String) request.get("tipo"));
        incident.setDescripcion((String) request.get("descripcion"));
        incident.setUbicacion((String) request.get("ubicacion"));
        incident.setContacto((String) request.get("contacto"));
        incident.setAtendido(false);
        incidentRepository.save(incident);
        
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
    
    // ==================== DEPURACIÓN ====================
    
    @GetMapping("/debug/users")
    public String debugUsers() {
        List<User> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Usuarios CupoTax</title><style>");
        sb.append("body{background:#0a0a0a;color:#fff;font-family:Arial;padding:20px;}");
        sb.append("table{border-collapse:collapse;width:100%;}");
        sb.append("th,td{border:1px solid #FFD700;padding:8px;text-align:left;}");
        sb.append("th{background:#FFD700;color:#000;}");
        sb.append("</style></head><body>");
        sb.append("<h1 style='color:#FFD700;'>📋 Usuarios registrados</h1>");
        sb.append("<p>Total: <strong>").append(users.size()).append("</strong> usuarios</p>");
        sb.append("<table>");
        sb.append("氢<th>ID</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Activo</th><th>Teléfono</th>");
        
        for (User u : users) {
            sb.append("<tr>");
            sb.append("<td>").append(u.getId()).append("</tr>");
            sb.append("<td>").append(u.getNombreCompleto() != null ? u.getNombreCompleto() : u.getUsername()).append("</tr>");
            sb.append("<td>").append(u.getEmail()).append("</td>");
            sb.append("<td>").append(u.getRol()).append("</td>");
            sb.append("<td style='color:").append(u.isActivo() ? "#2ecc71" : "#e74c3c").append("'>").append(u.isActivo() ? "✅ Activo" : "❌ Inactivo").append("</td>");
            sb.append("<td>").append(u.getTelefono() != null ? u.getTelefono() : "-").append("</td>");
            sb.append("</tr>");
        }
        
        sb.append("</table>");
        sb.append("<p style='margin-top:20px;'><a href='/' style='color:#FFD700;'>⬅ Volver a CupoTax</a></p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}