package com.cupotax.models;

import java.util.Date;

public class AuditLog {
    private String id;
    private String usuarioId;
    private String accion;
    private String detalles;
    private String ip;
    private Date fecha;
    private String navegador;

    public AuditLog() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getNavegador() { return navegador; }
    public void setNavegador(String navegador) { this.navegador = navegador; }
}