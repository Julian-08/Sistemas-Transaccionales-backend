package com.uniminuto.zimbra.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta generica para operaciones de guardar, actualizar o cambiar estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaRs {

    /** Indica si la operacion termino bien. */
    private Boolean exito;

    /** Mensaje para mostrar al usuario. */
    private String mensaje;

    /** Id del registro creado o modificado, si aplica. */
    private Integer id;

    public static RespuestaRs ok(String mensaje) {
        return new RespuestaRs(Boolean.TRUE, mensaje, null);
    }

    public static RespuestaRs ok(String mensaje, Integer id) {
        return new RespuestaRs(Boolean.TRUE, mensaje, id);
    }
}
