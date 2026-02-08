package com.afascl.volquetes.domain;

/**
 * Parámetro o solicitud inválida (ej. estado no permitido en query).
 * Mapeado a HTTP 400 por GlobalExceptionHandler.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
