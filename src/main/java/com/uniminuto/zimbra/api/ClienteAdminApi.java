package com.uniminuto.zimbra.api;

import com.uniminuto.zimbra.models.ClienteAdminRs;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.security.RolesPermitidos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Gestion de clientes por el administrador. Los clientes nunca se borran;
 * se inactivan tras DIAS_INACTIVIDAD dias sin ingresar y sin licencia.
 */
@RequestMapping("/cliente-admin")
@RolesPermitidos({Roles.ADMINISTRADOR})
public interface ClienteAdminApi {

    /**
     * Lista todos los clientes con ultimo ingreso, licencia y si se pueden inactivar.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<ClienteAdminRs>> listar();

    /**
     * El administrador crea un cliente (la contrasena se cifra con BCrypt).
     */
    @PostMapping(value = "/crear", produces = "application/json", consumes = "application/json")
    ResponseEntity<RespuestaRs> crear(@Valid @RequestBody RegistroClienteRq registroRq);

    /**
     * Inactiva un cliente sin licencia que lleva DIAS_INACTIVIDAD dias sin ingresar.
     */
    @PutMapping(value = "/{idCliente}/inactivar", produces = "application/json")
    ResponseEntity<RespuestaRs> inactivar(@PathVariable("idCliente") Integer idCliente);

    /**
     * Vuelve a activar un cliente.
     */
    @PutMapping(value = "/{idCliente}/activar", produces = "application/json")
    ResponseEntity<RespuestaRs> activar(@PathVariable("idCliente") Integer idCliente);

    /**
     * Inactiva a todos los clientes que cumplen la regla de inactividad.
     */
    @PostMapping(value = "/inactivar-inactivos", produces = "application/json")
    ResponseEntity<RespuestaRs> inactivarInactivos();
}
