package com.uniminuto.zimbra.exception;

import org.springframework.http.HttpStatus;

/**
 * 404: el registro solicitado no existe.
 */
public class RecursoNoEncontradoException extends BadRequestException {

    public RecursoNoEncontradoException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
