package com.uniminuto.zimbra.security;

/**
 * Datos del usuario que inicio sesion, tomados del token JWT.
 *
 * @param idUsuario id en la tabla usuarios.
 * @param nombre    nombre del usuario.
 * @param email     correo del usuario.
 * @param rol       ADMINISTRADOR, ASESOR o CLIENTE.
 * @param idAsesor  id en asesor_comercial (solo si es asesor).
 * @param idCliente id en clientes (solo si es cliente).
 */
public record UsuarioSesion(
        Integer idUsuario,
        String nombre,
        String email,
        String rol,
        Integer idAsesor,
        Integer idCliente) {

    public boolean esAdministrador() {
        return Roles.ADMINISTRADOR.equals(rol);
    }

    public boolean esAsesor() {
        return Roles.ASESOR.equals(rol);
    }

    public boolean esCliente() {
        return Roles.CLIENTE.equals(rol);
    }
}
