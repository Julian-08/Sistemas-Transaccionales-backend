package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.ClienteAdminRs;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;

import java.util.List;

/**
 * Gestion de clientes por el administrador. Los clientes nunca se borran:
 * se inactivan cuando llevan DIAS_INACTIVIDAD dias sin ingresar y no tienen licencia.
 */
public interface ClienteAdminService {

    List<ClienteAdminRs> listar();

    RespuestaRs crear(RegistroClienteRq registroRq);

    RespuestaRs inactivar(Integer idCliente);

    RespuestaRs activar(Integer idCliente);

    /** Inactiva de una vez a todos los clientes que cumplen la regla. */
    RespuestaRs inactivarInactivos();
}
