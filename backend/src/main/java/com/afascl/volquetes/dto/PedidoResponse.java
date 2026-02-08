package com.afascl.volquetes.dto;

import com.afascl.volquetes.domain.PedidoEstado;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * Response de pedido (listado y detalle). Contrato: memory-bank/04-api-documentation.md
 */
public class PedidoResponse {

    private Long id;
    private Long clienteId;
    private Long volqueteId;
    private String direccionEntrega;
    private PedidoEstado estado;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaCreacion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaEntregaPrevista;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaEntregaReal;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime fechaRetiroReal;
    private Long choferId;
    private Long camionId;
    private String clienteNombre;
    private String volqueteCodigoInterno;
    private String choferNombre;
    private String camionPatente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public PedidoEstado getEstado() {
        return estado;
    }

    public void setEstado(PedidoEstado estado) {
        this.estado = estado;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public OffsetDateTime getFechaEntregaPrevista() {
        return fechaEntregaPrevista;
    }

    public void setFechaEntregaPrevista(OffsetDateTime fechaEntregaPrevista) {
        this.fechaEntregaPrevista = fechaEntregaPrevista;
    }

    public OffsetDateTime getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(OffsetDateTime fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public OffsetDateTime getFechaRetiroReal() {
        return fechaRetiroReal;
    }

    public void setFechaRetiroReal(OffsetDateTime fechaRetiroReal) {
        this.fechaRetiroReal = fechaRetiroReal;
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

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getVolqueteCodigoInterno() {
        return volqueteCodigoInterno;
    }

    public void setVolqueteCodigoInterno(String volqueteCodigoInterno) {
        this.volqueteCodigoInterno = volqueteCodigoInterno;
    }

    public String getChoferNombre() {
        return choferNombre;
    }

    public void setChoferNombre(String choferNombre) {
        this.choferNombre = choferNombre;
    }

    public String getCamionPatente() {
        return camionPatente;
    }

    public void setCamionPatente(String camionPatente) {
        this.camionPatente = camionPatente;
    }
}
