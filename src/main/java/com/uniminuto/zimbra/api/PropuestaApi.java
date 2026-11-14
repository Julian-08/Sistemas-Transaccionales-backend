package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.CambiarEstadoPropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Propuestas comerciales (RFP), paso previo a la venta.
 */
@RequestMapping("/propuesta")
public interface PropuestaApi {

    /**
     * El asesor crea una propuesta para su prospecto.
     */
    @RolesPermitidos({Roles.ASESOR})
    @PostMapping(value = "/crear", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> crear(@Valid @RequestBody PropuestaRq propuestaRq);

    /**
     * Propuestas: el asesor ve las suyas; el administrador ve todas.
     */
    @RolesPermitidos({Roles.ASESOR, Roles.ADMINISTRADOR})
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<PropuestaRs>> listar(
            @RequestParam(value = "idCliente", required = false) Integer idCliente,
            @RequestParam(value = "estado", required = false) String estado);

    /**
     * El asesor pasa su propuesta a EN_REVISION o RECHAZADA.
     */
    @RolesPermitidos({Roles.ASESOR})
    @PutMapping(value = "/cambiar-estado", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> cambiarEstado(@Valid @RequestBody CambiarEstadoPropuestaRq estadoRq);
}
