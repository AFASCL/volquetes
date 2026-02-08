package com.afascl.volquetes.domain;

/**
 * Volquete no encontrado por ID.
 * Mapeado a HTTP 404 por GlobalExceptionHandler.
 */
public class VolqueteNotFoundException extends RuntimeException {

    public VolqueteNotFoundException(Long id) {
        super("Volquete no encontrado: id=" + id);
    }
}
