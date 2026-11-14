package com.uniminuto.zimbra.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Guarda en memoria las sesiones cerradas con "cerrar sesion" hasta que sus tokens
 * vencen, para que no se puedan volver a usar (RF-001: al cerrar sesion no se puede
 * acceder a paginas internas). Se guarda el id de la sesion, que es el mismo en
 * todos los tokens renovados de esa sesion.
 */
@Component
public class TokenRevocadoStore {

    private final Map<String, Instant> revocados = new ConcurrentHashMap<>();

    /**
     * @param idSesion   id de la sesion (claim jti del token).
     * @param hastaCuando momento a partir del cual ningun token de la sesion puede seguir vigente.
     */
    public void revocar(String idSesion, Instant hastaCuando) {
        limpiarVencidos();
        if (idSesion != null && hastaCuando != null) {
            revocados.put(idSesion, hastaCuando);
        }
    }

    public boolean estaRevocado(String idSesion) {
        return idSesion != null && revocados.containsKey(idSesion);
    }

    private void limpiarVencidos() {
        Instant ahora = Instant.now();
        revocados.entrySet().removeIf(entrada -> entrada.getValue().isBefore(ahora));
    }
}
