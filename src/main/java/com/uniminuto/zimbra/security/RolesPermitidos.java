package com.uniminuto.zimbra.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indica que roles pueden usar un endpoint (RNF-002).
 * Se puede poner en la interfaz Api completa o en cada metodo; el metodo tiene prioridad.
 * Ejemplo: {@code @RolesPermitidos({Roles.ADMINISTRADOR})}.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RolesPermitidos {

    String[] value();
}
