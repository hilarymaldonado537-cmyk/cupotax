package com.cupotax.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() { return "index"; }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/register") public String register() { return "register"; }
    @GetMapping("/admin") public String admin() { return "admin"; }
    @GetMapping("/taxista") public String taxista() { return "taxista"; }
    @GetMapping("/busero") public String busero() { return "busero"; }
    @GetMapping("/usuario") public String usuario() { return "usuario"; }
    @GetMapping("/viajes") public String viajes() { return "viajes"; }
    @GetMapping("/perfil") public String perfil() { return "perfil"; }
}