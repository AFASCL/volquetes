package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.OrigenEstado;
import com.afascl.volquetes.domain.VolqueteEstado;
import jakarta.validation.constraints.NotNull;

/**
 * Request para cambiar estado de volquete.
 * Contrato: memory-bank/04-api-documentation.md
 */
public class VolqueteEstadoRequest {

    @NotNull(message = "estado es obligatorio")
    private VolqueteEstado estado;

    /** Opcional: MANUAL | PEDIDO. Si se omite, se persiste NULL en historial. */
    private OrigenEstado origen;

    public VolqueteEstadoRequest() {
    }

    public VolqueteEstadoRequest(VolqueteEstado estado, OrigenEstado origen) {
        this.estado = estado;
        this.origen = origen;
    }

    public VolqueteEstado getEstado() {
        return estado;
    }

    public void setEstado(VolqueteEstado estado) {
        this.estado = estado;
    }

    public OrigenEstado getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenEstado origen) {
        this.origen = origen;
    }
}
