package com.cupotax.models;

import java.util.Date;

public class Notificacion {
    private String id;
    private String usuarioId;
    private String taxistaId;
    private String buseroId;
    private String titulo;
    private String mensaje;
    private String tipo;
    private Date fecha;
    private boolean leida;
    private String tripId;
    private String data;

    public Notificacion() {}

    public Notificacion(String usuarioId, String titulo, String mensaje, String tipo) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = new Date();
        this.leida = false;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getTaxistaId() { return taxistaId; }
    public void setTaxistaId(String taxistaId) { this.taxistaId = taxistaId; }
    public String getBuseroId() { return buseroId; }
    public void setBuseroId(String buseroId) { this.buseroId = buseroId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}