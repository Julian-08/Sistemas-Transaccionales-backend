package com.uniminuto.zimbra.security;

import com.uniminuto.zimbra.exception.NoAutorizadoException;
import io.jsonwebtoken.Claims;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Da acceso al usuario que hizo la peticion actual.
 * Los ApiController la usan para pasar la sesion a los servicios.
 */
public final class SesionActual {

    private SesionActual() {
    }

    public static UsuarioSesion obtener() {
        RequestAttributes atributos = RequestContextHolder.getRequestAttributes();
        Object sesion = atributos == null ? null
                : atributos.getAttribute(AutenticacionInterceptor.ATRIBUTO_SESION, RequestAttributes.SCOPE_REQUEST);
        if (!(sesion instanceof UsuarioSesion usuarioSesion)) {
            throw new NoAutorizadoException("Debe iniciar sesión para acceder a este recurso.");
        }
        return usuarioSesion;
    }

    /** Datos del token de la peticion actual (se usa para cerrar la sesion). */
    public static Claims claims() {
        RequestAttributes atributos = RequestContextHolder.getRequestAttributes();
        Object claims = atributos == null ? null
                : atributos.getAttribute(AutenticacionInterceptor.ATRIBUTO_CLAIMS, RequestAttributes.SCOPE_REQUEST);
        if (!(claims instanceof Claims tokenClaims)) {
            throw new NoAutorizadoException("Debe iniciar sesión para acceder a este recurso.");
        }
        return tokenClaims;
    }
}
