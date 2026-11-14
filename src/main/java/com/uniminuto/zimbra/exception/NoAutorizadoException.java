package com.uniminuto.zimbra.exception;

import org.springframework.http.HttpStatus;

/**
 * 401: el usuario no inicio sesion, el token no es valido o la sesion expiro (RF-001, RNF-003).
 */
public class NoAutorizadoException extends BadRequestException {

    public NoAutorizadoException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
