package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.ClienteTipo;

/**
 * Item para selector/combo (id, nombre, tipo).
 * Contrato: GET /api/clientes/selector
 */
public class ClienteSelectorItemResponse {

    private Long id;
    private String nombre;
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

    public ClienteTipo getTipo() {
        return tipo;
    }

    public void setTipo(ClienteTipo tipo) {
        this.tipo = tipo;
    }
}
