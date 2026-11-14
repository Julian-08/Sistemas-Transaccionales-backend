package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.CambiarEstadoPropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRq;
import com.uniminuto.zimbra.models.PropuestaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.util.List;

/**
 * Propuestas comerciales (RFP) del caso Zimbra.
 */
public interface PropuestaService {

    RespuestaRs crear(PropuestaRq propuestaRq, UsuarioSesion sesion);

    List<PropuestaRs> listar(UsuarioSesion sesion, Integer idCliente, String estado);

    RespuestaRs cambiarEstado(CambiarEstadoPropuestaRq estadoRq, UsuarioSesion sesion);
}
