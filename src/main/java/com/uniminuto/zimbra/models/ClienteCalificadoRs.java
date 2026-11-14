package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Cliente listo para ofrecerle el plan de pago: tiene el minimo de ingresos
 * configurado (INGRESOS_CALIFICADO) en la ventana de dias (DIAS_CALIFICADO).
 */
@Data
public class ClienteCalificadoRs {

    private Integer idCliente;
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String empresa;
    private Integer puntaje;
    private Boolean esProspecto;
    private String estadoProspecto;
    private Integer idAsesor;
    private String nombreAsesor;
    /** Ingresos dentro de la ventana de dias. */
    private Integer ingresosPeriodo;
    private LocalDateTime ultimoIngreso;
    private Boolean pruebaVigente;
    private LocalDateTime fechaVencimientoPrueba;
    /** true si ya compro una licencia. */
    private Boolean tieneLicencia;
}
