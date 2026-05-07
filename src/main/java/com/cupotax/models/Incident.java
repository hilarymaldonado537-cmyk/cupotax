package com.cupotax.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;
    
    private String tipo;
    private String descripcion;
    private String ubicacion;
    private String contacto;
    private boolean atendido = false;
    private LocalDateTime fecha = LocalDateTime.now();
    
    // ========== NUEVO CAMPO PARA FOTO DEL INCIDENTE ==========
    @Lob
    private byte[] fotoIncidente;
    // ========================================================
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    
    public boolean isAtendido() { return atendido; }
    public void setAtendido(boolean atendido) { this.atendido = atendido; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    // ========== GETTER Y SETTER PARA FOTO ==========
    public byte[] getFotoIncidente() { return fotoIncidente; }
    public void setFotoIncidente(byte[] fotoIncidente) { this.fotoIncidente = fotoIncidente; }
    // ===============================================
}