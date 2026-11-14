package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.SeguimientoRq;
import com.uniminuto.zimbra.models.SeguimientoRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.time.LocalDate;
import java.util.List;

/**
 * Seguimientos comerciales (RF-012, RF-018).
 */
public interface SeguimientoService {

    RespuestaRs registrar(SeguimientoRq seguimientoRq, UsuarioSesion sesion);

    List<SeguimientoRs> historialProspecto(Integer idCliente, UsuarioSesion sesion);

    List<SeguimientoRs> listar(Integer idAsesor, LocalDate desde, LocalDate hasta);
}
