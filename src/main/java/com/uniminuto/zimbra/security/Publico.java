package com.uniminuto.zimbra.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un endpoint que no necesita sesion (login, registro, recuperar contrasena).
 * Todo endpoint que no tenga esta anotacion exige un token valido.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Publico {
}
