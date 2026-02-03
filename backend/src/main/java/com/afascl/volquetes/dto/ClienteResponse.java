package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.ClienteTipo;

/**
 * Response de cliente (detalle y listado paginado).
 * Contrato: memory-bank/04-api-documentation.md
 */
public class ClienteResponse {

    private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private String direccionPrincipal;
    private ClienteTipo tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccionPrincipal() {
        return direccionPrincipal;
    }

    public void setDireccionPrincipal(String direccionPrincipal) {
        this.direccionPrincipal = direccionPrincipal;
    }

    public ClienteTipo getTipo() {
        return tipo;
    }

    public void setTipo(ClienteTipo tipo) {
        this.tipo = tipo;
    }
}
