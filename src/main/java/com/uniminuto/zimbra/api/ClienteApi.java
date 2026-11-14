package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.ActividadRq;
import com.uniminuto.zimbra.models.PerfilClienteRs;
import com.uniminuto.zimbra.models.PruebaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Opciones del cliente. Nunca devuelve puntaje ni seguimiento (RNF-004).
 */
@RequestMapping("/cliente")
@RolesPermitidos({Roles.CLIENTE})
public interface ClienteApi {

    /**
     * Perfil del cliente y su prueba vigente.
     */
    @GetMapping(value = "/perfil", produces = "application/json")
    ResponseEntity<PerfilClienteRs> obtenerPerfil();

    /**
     * Descarga la prueba gratuita de Zimbra (60 dias por defecto).
     */
    @PostMapping(value = "/descargar-prueba", produces = "application/json")
    ResponseEntity<RespuestaRs> descargarPrueba();

    /**
     * Historial de pruebas descargadas por el cliente.
     */
    @GetMapping(value = "/pruebas", produces = "application/json")
    ResponseEntity<List<PruebaRs>> listarPruebas();

    /**
     * Registra una pagina visitada y su tiempo de permanencia (RF-005).
     * Responde 202 de inmediato y guarda en segundo plano (RNF-006).
     */
    @PostMapping(value = "/actividad", consumes = "application/json")
    ResponseEntity<Void> registrarActividad(@Valid @RequestBody ActividadRq actividadRq);
}
