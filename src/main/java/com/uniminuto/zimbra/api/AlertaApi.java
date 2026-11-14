package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.AlertaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Alertas del asesor y del administrador (RF-008, RF-011).
 */
@RequestMapping("/alerta")
@RolesPermitidos({Roles.ASESOR, Roles.ADMINISTRADOR})
public interface AlertaApi {

    /**
     * Alertas del usuario: no leidas primero (RF-011).
     *
     * @param soloNoLeidas true = solo las no leidas.
     */
    @GetMapping(value = "/mis-alertas", produces = "application/json")
    ResponseEntity<List<AlertaRs>> listarMisAlertas(
            @RequestParam(value = "soloNoLeidas", defaultValue = "false") boolean soloNoLeidas);

    /**
     * Cantidad de alertas no leidas, para el contador del menu.
     */
    @GetMapping(value = "/no-leidas/conteo", produces = "application/json")
    ResponseEntity<Map<String, Long>> contarNoLeidas();

    /**
     * Marca una alerta como leida (RF-011).
     */
    @PutMapping(value = "/{idAlerta}/marcar-leida", produces = "application/json")
    ResponseEntity<RespuestaRs> marcarLeida(@PathVariable("idAlerta") Integer idAlerta);
}
