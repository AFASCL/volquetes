package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.PedidoEstado;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * Request para PATCH /api/pedidos/{id}/estado.
 * estado obligatorio; para ASIGNADO: choferId, camionId, fechaEntregaPrevista obligatorios.
 * Contrato: memory-bank/04-api-documentation.md
 */
public class PedidoEstadoRequest {

    @NotNull(message = "estado es obligatorio")
    private PedidoEstado estado;

    /** Obligatorio cuando estado destino es ASIGNADO. */
    private Long choferId;

    /** Obligatorio cuando estado destino es ASIGNADO. */
    private Long camionId;

    /** Obligatorio cuando estado destino es ASIGNADO. ISO-8601. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaEntregaPrevista;

    public PedidoEstado getEstado() {
        return estado;
    }

    public void setEstado(PedidoEstado estado) {
        this.estado = estado;
    }

    public Long getChoferId() {
        return choferId;
    }

    public void setChoferId(Long choferId) {
        this.choferId = choferId;
    }

    public Long getCamionId() {
        return camionId;
    }

    public void setCamionId(Long camionId) {
        this.camionId = camionId;
    }

    public OffsetDateTime getFechaEntregaPrevista() {
        return fechaEntregaPrevista;
    }

    public void setFechaEntregaPrevista(OffsetDateTime fechaEntregaPrevista) {
        this.fechaEntregaPrevista = fechaEntregaPrevista;
    }
}
