package com.duoc.pedidoservice.exception;

import java.time.LocalDateTime;

/// Clase que representa la estructura de la respuesta de error para las excepciones manejadas por el GlobalExceptionHandler.
public record ErrorResponse(
        String error,
        String mensaje,
        int status,
        LocalDateTime timestamp
) {}