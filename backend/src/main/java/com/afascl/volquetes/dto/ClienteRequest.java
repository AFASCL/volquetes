package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.ClienteTipo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request para crear y actualizar cliente.
 * Contrato: memory-bank/04-api-documentation.md
 */
public class ClienteRequest {

    @NotBlank(message = "nombre no puede estar vacío")
    @Size(max = 255)
    private String nombre;

    @Size(max = 50)
    private String telefono;

    @Email(message = "email debe ser válido")
    @Size(max = 255)
    private String email;

    @Size(max = 500)
    private String direccionPrincipal;

    @NotNull(message = "tipo es obligatorio")
    private ClienteTipo tipo;

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
