package com.afascl.volquetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Formato estándar de error de la API.
 * Contrato: memory-bank/04-api-documentation.md
 */
public class ErrorResponse {

    private String code;
    private String message;
    private List<String> details;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message, List<String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = OffsetDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
