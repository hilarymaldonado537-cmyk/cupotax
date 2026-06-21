package com.cupotax.models;

import java.util.Date;
import java.util.Map;

public class User {
    private String uid;
    private String nombre;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String cedula;
    private String username;
    private String rol;
    private String tipoBusero;
    private String prestataria;
    private String estado;
    private String pinSeguridad;
    private double saldo;
    private double calificacion;
    private int valoraciones;
    private Date fechaRegistro;
    private Date fechaActualizacion;
    private Date ultimaConexion;
    private Map<String, Object> vehiculo;
    private String fotoPerfil;
    private boolean premium;

    public User() {}

    public User(String uid, String nombre, String email, String rol) {
        this.uid = uid;
        this.nombre = nombre;
        this.nombreCompleto = nombre;
        this.email = email;
        this.rol = rol;
        this.estado = "Activo";
        this.saldo = 0;
        this.calificacion = 0;
        this.valoraciones = 0;
        this.fechaRegistro = new Date();
        this.fechaActualizacion = new Date();
    }

    // Getters y Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getTipoBusero() { return tipoBusero; }
    public void setTipoBusero(String tipoBusero) { this.tipoBusero = tipoBusero; }
    public String getPrestataria() { return prestataria; }
    public void setPrestataria(String prestataria) { this.prestataria = prestataria; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPinSeguridad() { return pinSeguridad; }
    public void setPinSeguridad(String pinSeguridad) { this.pinSeguridad = pinSeguridad; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public double getCalificacion() { return calificacion; }
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
    public int getValoraciones() { return valoraciones; }
    public void setValoraciones(int valoraciones) { this.valoraciones = valoraciones; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Date getUltimaConexion() { return ultimaConexion; }
    public void setUltimaConexion(Date ultimaConexion) { this.ultimaConexion = ultimaConexion; }
    public Map<String, Object> getVehiculo() { return vehiculo; }
    public void setVehiculo(Map<String, Object> vehiculo) { this.vehiculo = vehiculo; }
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public boolean isPremium() { return premium; }
    public void setPremium(boolean premium) { this.premium = premium; }
}