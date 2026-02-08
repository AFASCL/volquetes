package com.afascl.volquetes.domain;

/**
 * Estados de pedido. Contrato: memory-bank/04-api-documentation.md, 06-data-model.md
 */
public enum PedidoEstado {
    NUEVO,
    ASIGNADO,
    ENTREGADO,
    RETIRADO,
    CERRADO,
    CANCELADO
}
