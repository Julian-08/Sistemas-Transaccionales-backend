package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.AlertaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.util.List;

/**
 * Alertas del asesor y del administrador (RF-008, RF-011).
 */
public interface AlertaService {

    List<AlertaRs> listarMisAlertas(UsuarioSesion sesion, boolean soloNoLeidas);

    long contarNoLeidas(UsuarioSesion sesion);

    RespuestaRs marcarLeida(Integer idAlerta, UsuarioSesion sesion);
}
