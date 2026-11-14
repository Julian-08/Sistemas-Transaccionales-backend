package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.ActividadRq;
import com.uniminuto.zimbra.models.PerfilClienteRs;
import com.uniminuto.zimbra.models.PruebaRs;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.security.UsuarioSesion;

import java.util.List;

/**
 * Opciones del cliente: perfil, descarga de la prueba y registro de navegacion.
 * Nunca muestra puntaje ni seguimiento al cliente (RNF-004).
 */
public interface ClienteService {

    PerfilClienteRs obtenerPerfil(UsuarioSesion sesion);

    RespuestaRs descargarPrueba(UsuarioSesion sesion);

    List<PruebaRs> listarPruebas(UsuarioSesion sesion);

    /** RF-005: se guarda en segundo plano (RNF-006). */
    void registrarActividad(ActividadRq actividadRq, UsuarioSesion sesion);
}
