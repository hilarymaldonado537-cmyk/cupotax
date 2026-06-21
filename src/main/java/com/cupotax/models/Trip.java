package com.cupotax.models;

import java.util.Date;

public class Trip {
    private String id;
    private String usuarioId;
    private String taxistaId;
    private String buseroId;
    private String origen;
    private String destino;
    private double tarifa;
    private String estado;
    private String tipoViaje;
    private String tipoServicio;
    private int pasajeros;
    private double distancia;
    private Date fechaSolicitud;
    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaCreacion;
    private Date fechaActualizacion;
    private String calificacion;
    private String comentario;
    private String metodoPago;

    public Trip() {}

    public Trip(String usuarioId, String origen, String destino, double tarifa, String tipoViaje) {
        this.usuarioId = usuarioId;
        this.origen = origen;
        this.destino = destino;
        this.tarifa = tarifa;
        this.tipoViaje = tipoViaje;
        this.estado = "pendiente";
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
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
    
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    
    public double getTarifa() { return tarifa; }
    public void setTarifa(double tarifa) { this.tarifa = tarifa; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getTipoViaje() { return tipoViaje; }
    public void setTipoViaje(String tipoViaje) { this.tipoViaje = tipoViaje; }
    
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
    
    public int getPasajeros() { return pasajeros; }
    public void setPasajeros(int pasajeros) { this.pasajeros = pasajeros; }
    
    public double getDistancia() { return distancia; }
    public void setDistancia(double distancia) { this.distancia = distancia; }
    
    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public String getCalificacion() { return calificacion; }
    public void setCalificacion(String calificacion) { this.calificacion = calificacion; }
    
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}