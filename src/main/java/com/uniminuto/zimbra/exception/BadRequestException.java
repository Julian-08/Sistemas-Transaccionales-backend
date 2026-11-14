package com.uniminuto.zimbra.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepcion base del negocio. Por defecto responde 400 (Bad Request);
 * las subclases cambian el codigo HTTP (401, 403, 404).
 */
public class BadRequestException extends RuntimeException {

    private final HttpStatus status;

    public BadRequestException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    protected BadRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
