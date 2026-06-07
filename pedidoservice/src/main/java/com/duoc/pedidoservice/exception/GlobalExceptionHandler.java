package com.duoc.pedidoservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

// Clase encargada de manejar las excepciones de manera global en la aplicación,
// proporcionando respuestas estructuradas y adecuadas para cada tipo de error.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RecursoNoEncontradoException ex) {

        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "Recurso no encontrado",
                ex.getMessage(),
                404,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {

        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "Solicitud inválida",
                ex.getMessage(),
                400,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {

        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "Error interno",
                ex.getMessage(),
                500,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}