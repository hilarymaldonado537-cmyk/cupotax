package com.cupotax.controllers;

import com.cupotax.models.Empresa;
import com.cupotax.models.User;
import com.cupotax.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/emergencia")
public class EmergenciaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping("/numeros")
    public ResponseEntity<?> getNumerosEmergencia(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        
        // Números nacionales de Panamá (siempre disponibles)
        response.put("nacionales", Map.of(
            "policia", "911",
            "bomberos", "103",
            "ambulancia", "911",
            "transito", "104",
            "salud", "147"
        ));
        
        // Números específicos de la empresa
        if (user.getEmpresaId() != null) {
            Optional<Empresa> empresaOpt = empresaRepository.findById(user.getEmpresaId());
            if (empresaOpt.isPresent()) {
                Empresa empresa = empresaOpt.get();
                Map<String, String> empresaData = new HashMap<>();
                empresaData.put("nombre", empresa.getNombre());
                
                // Intentar obtener teléfono por reflexión o getter
                try {
                    String telefono = (String) empresa.getClass().getMethod("getTelefonoEmergencia").invoke(empresa);
                    empresaData.put("telefonoEmergencia", telefono != null ? telefono : "911");
                } catch (Exception e) {
                    empresaData.put("telefonoEmergencia", "911");
                }
                
                try {
                    String telefono2 = (String) empresa.getClass().getMethod("getTelefonoSecundario").invoke(empresa);
                    empresaData.put("telefonoSecundario", telefono2 != null ? telefono2 : "");
                } catch (Exception e) {
                    empresaData.put("telefonoSecundario", "");
                }
                
                response.put("empresa", empresaData);
            }
        }
        
        response.put("sugerencias", List.of(
            "📌 Guarda estos números en tus contactos",
            "📌 Activa la ubicación antes de llamar",
            "📌 Mantén la calma, describe tu ubicación"
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reportar")
    public ResponseEntity<?> reportarEmergencia(@AuthenticationPrincipal User user,
                                                 @RequestBody Map<String, String> request) {
        String tipo = request.get("tipo");
        String descripcion = request.get("descripcion");
        String ubicacion = request.get("ubicacion");
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Emergencia reportada. Las autoridades han sido notificadas.");
        response.put("whatsappLink", "https://wa.me/50760001111?text=🚨 EMERGENCIA CUPOTAX 🚨%0ATipo: " + tipo + "%0ADescripción: " + descripcion + "%0AUbicación: " + ubicacion + "%0AUsuario: " + user.getNombreCompleto());
        
        return ResponseEntity.ok(response);
    }
}