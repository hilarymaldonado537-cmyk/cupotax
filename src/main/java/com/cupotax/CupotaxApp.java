package com.cupotax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CupotaxApp {
    public static void main(String[] args) {
        SpringApplication.run(CupotaxApp.class, args);
        System.out.println("========================================");
        System.out.println("     CUPOTAX - SISTEMA DE TRANSPORTE");
        System.out.println("========================================");
        System.out.println(" 🚀 http://localhost:8080");
        System.out.println("========================================");
    }
}