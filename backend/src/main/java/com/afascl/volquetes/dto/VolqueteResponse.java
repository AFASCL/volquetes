package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.VolqueteEstado;

/**
 * Response de volquete (detalle y listado paginado).
 * Contrato: memory-bank/04-api-documentation.md
 */
public class VolqueteResponse {

    private Long id;
    private String codigoInterno;
    private String codigoExterno;
    private VolqueteEstado estadoActual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public VolqueteEstado getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(VolqueteEstado estadoActual) {
        this.estadoActual = estadoActual;
    }
}
