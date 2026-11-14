package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.VentaRq;
import com.uniminuto.zimbra.models.VentaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Ventas de licencias, transaccion final del proceso comercial.
 */
@RequestMapping("/venta")
public interface VentaApi {

    /**
     * El asesor cierra la venta sobre una de sus propuestas.
     */
    @RolesPermitidos({Roles.ASESOR})
    @PostMapping(value = "/registrar", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> registrar(@Valid @RequestBody VentaRq ventaRq);

    /**
     * Ventas: el asesor ve las suyas; el administrador ve todas.
     */
    @RolesPermitidos({Roles.ASESOR, Roles.ADMINISTRADOR})
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<VentaRs>> listar(
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta);
}
