package com.afascl.volquetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * Request para crear y actualizar pedido. Contrato: memory-bank/04-api-documentation.md
 */
public class PedidoRequest {

    @NotNull(message = "clienteId es obligatorio")
    private Long clienteId;

    @NotNull(message = "volqueteId es obligatorio")
    private Long volqueteId;

    @NotBlank(message = "direccionEntrega no puede estar vacía")
    @Size(max = 500)
    private String direccionEntrega;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaEntregaPrevista;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getVolqueteId() {
        return volqueteId;
    }

    public void setVolqueteId(Long volqueteId) {
        this.volqueteId = volqueteId;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public OffsetDateTime getFechaEntregaPrevista() {
        return fechaEntregaPrevista;
    }

    public void setFechaEntregaPrevista(OffsetDateTime fechaEntregaPrevista) {
        this.fechaEntregaPrevista = fechaEntregaPrevista;
    }
}
