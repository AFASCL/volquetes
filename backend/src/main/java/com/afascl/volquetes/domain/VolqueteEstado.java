package com.afascl.volquetes.domain;

/**
 * Estado del volquete en inventario.
 * Persistido como String en DB (VARCHAR + CHECK). Contrato: memory-bank/04-api-documentation.md
 */
public enum VolqueteEstado {
    DISPONIBLE,
    EN_CLIENTE,
    EN_TRANSITO,
    FUERA_DE_SERVICIO
}
