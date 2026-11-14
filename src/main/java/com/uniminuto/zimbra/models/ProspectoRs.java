package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Prospecto con su puntaje, estado y asesor asignado (RF-009, RF-016).
 */
@Data
public class ProspectoRs {

    private Integer idCliente;
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String empresa;
    private Integer puntaje;
    private String estadoProspecto;
    private LocalDateTime fechaClasificacion;
    private Integer idAsesor;
    private String nombreAsesor;
    private LocalDateTime ultimaActividad;
}
