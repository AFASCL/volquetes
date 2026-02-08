package com.afascl.volquetes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Entidad Historial de cambios de estado de volquete. Tabla volquete_estado_historial.
 * Contrato: memory-bank/06-data-model.md
 */
@Entity
@Table(name = "volquete_estado_historial")
public class VolqueteEstadoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "volquete_id", nullable = false)
    private Volquete volquete;

    @Column(name = "estado_desde", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VolqueteEstado estadoDesde;

    @Column(name = "estado_hasta", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VolqueteEstado estadoHasta;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    @Column(name = "origen", length = 10)
    @Enumerated(EnumType.STRING)
    private OrigenEstado origen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Volquete getVolquete() {
        return volquete;
    }

    public void setVolquete(Volquete volquete) {
        this.volquete = volquete;
    }

    public VolqueteEstado getEstadoDesde() {
        return estadoDesde;
    }

    public void setEstadoDesde(VolqueteEstado estadoDesde) {
        this.estadoDesde = estadoDesde;
    }

    public VolqueteEstado getEstadoHasta() {
        return estadoHasta;
    }

    public void setEstadoHasta(VolqueteEstado estadoHasta) {
        this.estadoHasta = estadoHasta;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public OrigenEstado getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenEstado origen) {
        this.origen = origen;
    }

    @PrePersist
    void onPersist() {
        if (this.fechaHora == null) {
            this.fechaHora = OffsetDateTime.now();
        }
    }
}
