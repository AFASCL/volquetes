package com.afascl.volquetes.domain;

/**
 * Cliente no encontrado por ID.
 * Mapeado a HTTP 404 por GlobalExceptionHandler.
 */
public class ClienteNotFoundException extends RuntimeException {

    public ClienteNotFoundException(Long id) {
        super("Cliente no encontrado: id=" + id);
    }
}
