package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.VentaRq;
import com.uniminuto.zimbra.models.VentaRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.time.LocalDate;
import java.util.List;

/**
 * Ventas de licencias del caso Zimbra.
 */
public interface VentaService {

    RespuestaRs registrar(VentaRq ventaRq, UsuarioSesion sesion);

    List<VentaRs> listar(UsuarioSesion sesion, LocalDate desde, LocalDate hasta);
}
