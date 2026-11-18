package com.uniminuto.zimbra.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de "olvide mi contrasena" (RF-003).
 * Nunca incluye el codigo: este llega por correo (o, en desarrollo, se consulta en la base).
 * Es la misma respuesta exista o no la cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecuperacionRs {

    private Boolean exito;

    private String mensaje;

    /** Minutos que dura el codigo. */
    private Integer minutosVigencia;
}
