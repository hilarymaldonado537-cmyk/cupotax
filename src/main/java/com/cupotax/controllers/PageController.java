package com.cupotax.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }
    
    @GetMapping("/usuario")
    public String usuarioPage() {
        return "usuario";
    }
    
    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }
    
    @GetMapping("/taxista")
    public String taxistaPage() {
        return "taxista";
    }
    
    @GetMapping("/busero")
    public String buseroPage() {
        return "busero";
    }
    
    @GetMapping("/estudiante")
    public String estudiantePage() {
        return "usuario";
    }
}