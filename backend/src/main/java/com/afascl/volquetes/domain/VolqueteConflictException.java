package com.afascl.volquetes.domain;

/**
 * Conflicto de unicidad (codigo_interno o codigo_externo duplicado).
 * Mapeado a HTTP 409 por GlobalExceptionHandler.
 */
public class VolqueteConflictException extends RuntimeException {

    public VolqueteConflictException(String message) {
        super(message);
    }
}
