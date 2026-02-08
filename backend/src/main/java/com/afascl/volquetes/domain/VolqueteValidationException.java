package com.afascl.volquetes.domain;

/**
 * Violación de regla de negocio o validación de volquete.
 * Mapeado a HTTP 422 por GlobalExceptionHandler.
 */
public class VolqueteValidationException extends RuntimeException {

    public VolqueteValidationException(String message) {
        super(message);
    }
}
