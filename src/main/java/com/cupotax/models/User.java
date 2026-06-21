package com.cupotax.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombreCompleto;
    
    @Column(unique = true, nullable = false)
    private String cedula;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String telefono;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String rol;
    
    @Column(nullable = false, length = 6)
    private String pinSeguridad;
    
    @Lob
    private byte[] fotoPerfil;
    
    @Lob
    private byte[] fotoBus;
    
    private boolean activo = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Campos para recuperación de contraseña
    private String codigoRecuperacion;
    private String codigoRecuperacionExpiracion;
    
    // Campos para taxista/busero (vehículo)
    private String placa;
    private String modelo;
    private String color;
    private Integer capacidad;
    private String tipoTaxi;
    private String rutaBusero;
    private Integer anioVehiculo;
    private String numeroBus;
    private String chasis;
    private LocalDateTime fechaMatricula;
    private LocalDateTime ultimaRevision;
    private LocalDateTime proximaRevision;
    
    @Lob
    private byte[] fotoVehiculo;
    
    // Ubicación GPS
    private double latitud = 0.0;
    private double longitud = 0.0;
    
    // Campo para ruta seleccionada en el registro
    private String rutaSeleccionada;
    
    // ========== GETTERS Y SETTERS ==========
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getPinSeguridad() { return pinSeguridad; }
    public void setPinSeguridad(String pinSeguridad) { this.pinSeguridad = pinSeguridad; }
    
    public byte[] getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(byte[] fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    
    public byte[] getFotoBus() { return fotoBus; }
    public void setFotoBus(byte[] fotoBus) { this.fotoBus = fotoBus; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getCodigoRecuperacion() { return codigoRecuperacion; }
    public void setCodigoRecuperacion(String codigoRecuperacion) { this.codigoRecuperacion = codigoRecuperacion; }
    
    public String getCodigoRecuperacionExpiracion() { return codigoRecuperacionExpiracion; }
    public void setCodigoRecuperacionExpiracion(String codigoRecuperacionExpiracion) { this.codigoRecuperacionExpiracion = codigoRecuperacionExpiracion; }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    
    public String getTipoTaxi() { return tipoTaxi; }
    public void setTipoTaxi(String tipoTaxi) { this.tipoTaxi = tipoTaxi; }
    
    public String getRutaBusero() { return rutaBusero; }
    public void setRutaBusero(String rutaBusero) { this.rutaBusero = rutaBusero; }
    
    public Integer getAnioVehiculo() { return anioVehiculo; }
    public void setAnioVehiculo(Integer anioVehiculo) { this.anioVehiculo = anioVehiculo; }
    
    public String getNumeroBus() { return numeroBus; }
    public void setNumeroBus(String numeroBus) { this.numeroBus = numeroBus; }
    
    public String getChasis() { return chasis; }
    public void setChasis(String chasis) { this.chasis = chasis; }
    
    public LocalDateTime getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(LocalDateTime fechaMatricula) { this.fechaMatricula = fechaMatricula; }
    
    public LocalDateTime getUltimaRevision() { return ultimaRevision; }
    public void setUltimaRevision(LocalDateTime ultimaRevision) { this.ultimaRevision = ultimaRevision; }
    
    public LocalDateTime getProximaRevision() { return proximaRevision; }
    public void setProximaRevision(LocalDateTime proximaRevision) { this.proximaRevision = proximaRevision; }
    
    public byte[] getFotoVehiculo() { return fotoVehiculo; }
    public void setFotoVehiculo(byte[] fotoVehiculo) { this.fotoVehiculo = fotoVehiculo; }
    
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    
    public String getRutaSeleccionada() { return rutaSeleccionada; }
    public void setRutaSeleccionada(String rutaSeleccionada) { this.rutaSeleccionada = rutaSeleccionada; }
}