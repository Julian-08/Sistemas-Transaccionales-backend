package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.LoginRq;
import com.uniminuto.zimbra.models.LoginRs;
import com.uniminuto.zimbra.models.MenuOpcionRs;
import com.uniminuto.zimbra.models.RecuperacionRs;
import com.uniminuto.zimbra.models.RecuperarContrasenaRq;
import com.uniminuto.zimbra.models.RegistroClienteRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.RestablecerContrasenaRq;
import com.uniminuto.zimbra.security.UsuarioSesion;
import io.jsonwebtoken.Claims;

import java.util.List;

/**
 * Inicio y cierre de sesion, registro de clientes y recuperacion de contrasena.
 */
public interface AuthService {

    /** RF-001, RF-002, RF-004: valida credenciales, registra el ingreso y devuelve token y menu. */
    LoginRs login(LoginRq loginRq);

    /** RF-001: invalida el token actual. */
    RespuestaRs cerrarSesion(Claims claims);

    /** Registro de un cliente desde el sitio web. */
    RespuestaRs registrarCliente(RegistroClienteRq registroRq);

    /** RF-003: envia el enlace de recuperacion; la respuesta no revela si el correo existe. */
    RecuperacionRs solicitarRecuperacion(RecuperarContrasenaRq recuperarRq);

    /** RF-003: guarda la nueva contrasena cifrada si el enlace es valido. */
    RespuestaRs restablecerContrasena(RestablecerContrasenaRq restablecerRq);

    /** RF-002: menu del usuario segun su rol. */
    List<MenuOpcionRs> obtenerMenu(UsuarioSesion sesion);
}
