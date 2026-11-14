package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.entity.Rol;
import com.uniminuto.zimbra.models.AsignarRolRq;
import com.uniminuto.zimbra.models.CambiarEstadoUsuarioRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.models.UsuarioActualizarRq;
import com.uniminuto.zimbra.models.UsuarioRq;
import com.uniminuto.zimbra.models.UsuarioRs;

import java.util.List;

/**
 * Gestion de usuarios y roles por el administrador (RF-014, RF-015).
 */
public interface UsuarioService {

    List<UsuarioRs> listar(Integer idRol);

    UsuarioRs obtener(Integer idUsuario);

    RespuestaRs crear(UsuarioRq usuarioRq);

    RespuestaRs actualizar(UsuarioActualizarRq actualizarRq);

    RespuestaRs cambiarEstado(CambiarEstadoUsuarioRq estadoRq);

    RespuestaRs asignarRol(AsignarRolRq asignarRolRq);

    List<Rol> listarRoles();
}
