package com.uniminuto.zimbra.models;

import lombok.Data;

import java.util.List;

/**
 * Respuesta del inicio de sesion: token, datos basicos y menu del rol (RF-001, RF-002).
 */
@Data
public class LoginRs {

    /** Token JWT que se envia en el header Authorization: Bearer token. */
    private String token;

    private String tipoToken = "Bearer";

    /** Minutos de inactividad antes de que la sesion venza (RNF-003). */
    private Long minutosInactividad;

    private Integer idUsuario;
    private String nombre;
    private String email;
    private String rol;

    /** Opciones del menu segun el rol (RF-002). */
    private List<MenuOpcionRs> menu;
}
