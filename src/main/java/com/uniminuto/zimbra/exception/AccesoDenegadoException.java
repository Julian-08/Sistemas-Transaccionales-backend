package com.uniminuto.zimbra.exception;

import org.springframework.http.HttpStatus;

/**
 * 403: el usuario inicio sesion pero su rol no le permite la accion (RNF-002, RNF-004).
 */
public class AccesoDenegadoException extends BadRequestException {

    public AccesoDenegadoException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
