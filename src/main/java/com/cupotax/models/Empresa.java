package com.cupotax.models;

import java.util.Date;

public class Empresa {
    private String id;
    private String nombre;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;
    private String logo;
    private String color;
    private Date fechaRegistro;
    private int taxistas;
    private int buseros;

    public Empresa() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public int getTaxistas() { return taxistas; }
    public void setTaxistas(int taxistas) { this.taxistas = taxistas; }
    public int getBuseros() { return buseros; }
    public void setBuseros(int buseros) { this.buseros = buseros; }
}