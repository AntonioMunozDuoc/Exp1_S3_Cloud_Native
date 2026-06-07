package com.duoc.pedidoservice.exception;

/// Excepción personalizada que se lanza cuando un recurso no es encontrado en la base de datos.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}