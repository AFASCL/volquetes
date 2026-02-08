package com.afascl.volquetes.controller.advice;

import com.afascl.volquetes.domain.BadRequestException;
import com.afascl.volquetes.domain.ClienteNotFoundException;
import com.afascl.volquetes.domain.ClienteValidationException;
import com.afascl.volquetes.domain.PedidoConflictException;
import com.afascl.volquetes.domain.PedidoNotFoundException;
import com.afascl.volquetes.domain.PedidoValidationException;
import com.afascl.volquetes.domain.VolqueteConflictException;
import com.afascl.volquetes.domain.VolqueteNotFoundException;
import com.afascl.volquetes.domain.VolqueteValidationException;
import com.afascl.volquetes.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejo de errores de la API. Formato estándar: code, message, details, timestamp.
 * memory-bank/04-api-documentation.md
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ClienteNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(ClienteValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ClienteValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestParam(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(VolqueteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVolqueteNotFound(VolqueteNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(VolqueteValidationException.class)
    public ResponseEntity<ErrorResponse> handleVolqueteValidation(VolqueteValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(VolqueteConflictException.class)
    public ResponseEntity<ErrorResponse> handleVolqueteConflict(VolqueteConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(PedidoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePedidoNotFound(PedidoNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(PedidoValidationException.class)
    public ResponseEntity<ErrorResponse> handlePedidoValidation(PedidoValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(PedidoConflictException.class)
    public ResponseEntity<ErrorResponse> handlePedidoConflict(PedidoConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + (e.getDefaultMessage() != null ? e.getDefaultMessage() : "inválido"))
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("VALIDATION_ERROR", "Error de validación", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpMessageNotReadableException ex) {
        String message = ex.getMessage() != null && ex.getMessage().contains("JSON") ? "JSON inválido" : "Cuerpo de solicitud inválido";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", message, List.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor", List.of()));
    }
}
