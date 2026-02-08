package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.VolqueteEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request para crear (y actualizar) volquete.
 * POST: codigoInterno, codigoExterno obligatorios; estadoInicial opcional (default DISPONIBLE).
 * PUT: solo codigoInterno y codigoExterno (estadoInicial se ignora).
 * Contrato: memory-bank/04-api-documentation.md
 */
public class VolqueteRequest {

    @NotBlank(message = "codigoInterno no puede estar vacío")
    @Size(max = 50)
    private String codigoInterno;

    @NotBlank(message = "codigoExterno no puede estar vacío")
    @Size(max = 50)
    private String codigoExterno;

    /** Solo aplica en POST; en PUT se ignora. Default DISPONIBLE si null. */
    private VolqueteEstado estadoInicial;

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

    public VolqueteEstado getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(VolqueteEstado estadoInicial) {
        this.estadoInicial = estadoInicial;
    }
}
