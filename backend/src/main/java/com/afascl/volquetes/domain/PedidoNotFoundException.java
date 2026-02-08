package com.afascl.volquetes.domain;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long id) {
        super("Pedido no encontrado: " + id);
    }
}
