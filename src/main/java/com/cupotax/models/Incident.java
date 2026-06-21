package com.cupotax.models;

import java.util.Date;

public class Incident {
    private String id;
    private String usuarioId;
    private String taxistaId;
    private String buseroId;
    private String tipo;
    private String descripcion;
    private String ubicacion;
    private String estado;
    private Date fecha;
    private Date fechaResolucion;
    private double latitud;
    private double longitud;
    private String foto;

    public Incident() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getTaxistaId() { return taxistaId; }
    public void setTaxistaId(String taxistaId) { this.taxistaId = taxistaId; }
    public String getBuseroId() { return buseroId; }
    public void setBuseroId(String buseroId) { this.buseroId = buseroId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public Date getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(Date fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}