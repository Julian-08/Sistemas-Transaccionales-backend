package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.entity.Configuracion;
import com.uniminuto.zimbra.models.ConfiguracionRq;
import com.uniminuto.zimbra.models.PuntajeMinimoRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Parametros del sistema. Solo administrador (RF-017).
 */
@RequestMapping("/configuracion")
@RolesPermitidos({Roles.ADMINISTRADOR})
public interface ConfiguracionApi {

    /**
     * Lista todos los parametros.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Configuracion>> listar();

    /**
     * Puntaje minimo actual para ser prospecto.
     */
    @GetMapping(value = "/puntaje-minimo", produces = "application/json")
    ResponseEntity<Map<String, Integer>> obtenerPuntajeMinimo();

    /**
     * Cambia un parametro numerico: PUNTAJE_MINIMO, DIAS_PRUEBA, INGRESOS_CALIFICADO,
     * DIAS_CALIFICADO o DIAS_INACTIVIDAD.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> actualizar(@Valid @RequestBody ConfiguracionRq configuracionRq);

    /**
     * Cambia el puntaje minimo; aplica sin reiniciar (RF-017).
     */
    @PutMapping(value = "/puntaje-minimo", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> actualizarPuntajeMinimo(@Valid @RequestBody PuntajeMinimoRq puntajeRq);
}
