package com.uniminuto.zimbra.security;

import com.uniminuto.zimbra.exception.NoAutorizadoException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Crea y valida los tokens JWT de sesion (RF-001).
 * El token vence tras los minutos de inactividad configurados (RNF-003);
 * cada peticion valida devuelve un token renovado.
 */
@Service
public class JwtService {

    private static final int LONGITUD_MINIMA_SECRETO = 32;

    private final SecretKey clave;
    private final long minutosInactividad;

    public JwtService(@Value("${zimbra.jwt.secret}") String secreto,
                      @Value("${zimbra.jwt.minutos-inactividad:15}") long minutosInactividad) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < LONGITUD_MINIMA_SECRETO) {
            throw new IllegalStateException(
                    "zimbra.jwt.secret debe tener al menos " + LONGITUD_MINIMA_SECRETO + " caracteres.");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.minutosInactividad = minutosInactividad;
    }

    /** Genera el token de una sesion nueva (inicio de sesion). */
    public String generarToken(UsuarioSesion sesion) {
        return generarToken(sesion, UUID.randomUUID().toString());
    }

    /**
     * Genera un token firmado con los datos de la sesion.
     *
     * @param idSesion id de la sesion; se conserva al renovar el token para que
     *                 "cerrar sesion" invalide tambien los tokens renovados.
     */
    public String generarToken(UsuarioSesion sesion, String idSesion) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .id(idSesion)
                .subject(String.valueOf(sesion.idUsuario()))
                .claim("nombre", sesion.nombre())
                .claim("email", sesion.email())
                .claim("rol", sesion.rol())
                .claim("idAsesor", sesion.idAsesor())
                .claim("idCliente", sesion.idCliente())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(minutosInactividad, ChronoUnit.MINUTES)))
                .signWith(clave)
                .compact();
    }

    /** Valida firma y vencimiento; si falla lanza 401. */
    public Claims validar(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new NoAutorizadoException(
                    "Su sesión expiró por inactividad. Inicie sesión nuevamente.");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new NoAutorizadoException("El token de sesión no es válido. Inicie sesión nuevamente.");
        }
    }

    /** Convierte los datos del token en la sesion del usuario. */
    public UsuarioSesion aSesion(Claims claims) {
        return new UsuarioSesion(
                Integer.valueOf(claims.getSubject()),
                claims.get("nombre", String.class),
                claims.get("email", String.class),
                claims.get("rol", String.class),
                entero(claims.get("idAsesor")),
                entero(claims.get("idCliente")));
    }

    public long getMinutosInactividad() {
        return minutosInactividad;
    }

    private Integer entero(Object valor) {
        return valor instanceof Number numero ? numero.intValue() : null;
    }
}
