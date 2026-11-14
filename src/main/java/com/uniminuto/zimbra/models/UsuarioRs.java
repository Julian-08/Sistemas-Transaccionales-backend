package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de un usuario para el administrador. Nunca incluye la contrasena ni su hash.
 */
@Data
public class UsuarioRs {

    private Integer idUsuario;
    private String nombre;
    private String email;
    private Integer idRol;
    private String rol;
    private String estado;
    private LocalDateTime fechaCreacion;
}
