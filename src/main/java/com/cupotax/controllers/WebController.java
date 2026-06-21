package com.cupotax.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    // ========== PÁGINAS PRINCIPALES ==========
    @GetMapping("/")
    public String index() { 
        return "index"; 
    }
    
    @GetMapping("/login")
    public String login() { 
        return "login"; 
    }
    
    @GetMapping("/register")
    public String register() { 
        return "register"; 
    }
    
    @GetMapping("/viajes")
    public String viajes() { 
        return "viajes"; 
    }
    
    // ========== PERFILES ==========
    @GetMapping("/perfil")
    public String perfil() { 
        return "perfil"; 
    }
    
    @GetMapping("/perfil-usuario")
    public String perfilUsuario() { 
        return "perfil-usuario"; 
    }
    
    @GetMapping("/perfil-taxista")
    public String perfilTaxista() { 
        return "perfil-taxista"; 
    }
    
    @GetMapping("/perfil-busero")
    public String perfilBusero() { 
        return "perfil-busero"; 
    }
    
    // ========== DASHBOARDS ==========
    @GetMapping("/dashboard-admin")
    public String dashboardAdmin() { 
        return "dashboard-admin"; 
    }
    
    @GetMapping("/dashboard-taxista")
    public String dashboardTaxista() { 
        return "dashboard-taxista"; 
    }
    
    @GetMapping("/dashboard-busero")
    public String dashboardBusero() { 
        return "dashboard-busero"; 
    }
    
    @GetMapping("/dashboard-usuario")
    public String dashboardUsuario() { 
        return "dashboard-usuario"; 
    }
    
    // ========== ROLES ORIGINALES (pestañas de reservas) ==========
    @GetMapping("/taxista")
    public String taxista() { 
        return "taxista"; 
    }
    
    @GetMapping("/busero")
    public String busero() { 
        return "busero"; 
    }
    
    @GetMapping("/usuario")
    public String usuario() { 
        return "usuario"; 
    }
    
    // ========== ADMIN - COMENTADO para evitar conflicto ==========
    // El AdminController.java ya maneja la ruta /admin
    // Si quieres mantenerlo, descomenta y cambia la ruta a /admin-panel
    // @GetMapping("/admin-panel")
    // public String admin() { 
    //     return "admin"; 
    // }
}