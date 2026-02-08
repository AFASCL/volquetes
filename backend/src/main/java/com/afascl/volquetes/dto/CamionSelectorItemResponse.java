package com.afascl.volquetes.dto;

/**
 * Item para selector de camiones. Contrato: GET /api/camiones/selector
 */
public class CamionSelectorItemResponse {

    private Long id;
    private String patente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }
}
