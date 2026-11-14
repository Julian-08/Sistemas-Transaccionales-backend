package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Perfil que ve el propio cliente. No incluye puntaje ni seguimiento (RNF-004).
 */
@Data
public class PerfilClienteRs {

    private Integer idCliente;
    private String nombre;
    private String email;
    private String empresa;
    private LocalDateTime fechaRegistro;
    private PruebaRs pruebaVigente;
}
