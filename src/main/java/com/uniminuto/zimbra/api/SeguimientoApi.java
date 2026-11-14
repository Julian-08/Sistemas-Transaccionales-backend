package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.SeguimientoRq;
import com.uniminuto.zimbra.models.SeguimientoRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Seguimientos comerciales (RF-012, RF-018).
 */
@RequestMapping("/seguimiento")
public interface SeguimientoApi {

    /**
     * El asesor registra un contacto (llamada o correo) con su prospecto (RF-012).
     */
    @RolesPermitidos({Roles.ASESOR})
    @PostMapping(value = "/registrar", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> registrar(@Valid @RequestBody SeguimientoRq seguimientoRq);

    /**
     * Historial de seguimientos de un prospecto.
     */
    @RolesPermitidos({Roles.ASESOR, Roles.ADMINISTRADOR})
    @GetMapping(value = "/prospecto/{idCliente}", produces = "application/json")
    ResponseEntity<List<SeguimientoRs>> historialProspecto(@PathVariable("idCliente") Integer idCliente);

    /**
     * Todos los seguimientos con filtros por asesor y fecha (RF-018).
     *
     * @param desde fecha inicial (yyyy-MM-dd), opcional.
     * @param hasta fecha final incluida (yyyy-MM-dd), opcional.
     */
    @RolesPermitidos({Roles.ADMINISTRADOR})
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<SeguimientoRs>> listar(
            @RequestParam(value = "idAsesor", required = false) Integer idAsesor,
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta);
}
