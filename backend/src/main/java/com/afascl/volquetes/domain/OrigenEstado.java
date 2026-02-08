package com.afascl.volquetes.domain;

/**
 * Origen de cambio de estado del volquete.
 * Persistido como String en DB (VARCHAR + CHECK). Contrato: memory-bank/04-api-documentation.md
 */
public enum OrigenEstado {
    MANUAL,
    PEDIDO
}
