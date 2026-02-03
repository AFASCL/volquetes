package com.afascl.volquetes.domain;

/**
 * Violación de regla de negocio o validación de cliente.
 * Mapeado a HTTP 422 por GlobalExceptionHandler.
 */
public class ClienteValidationException extends RuntimeException {

    public ClienteValidationException(String message) {
        super(message);
    }
}
