package com.uniminuto.zimbra.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Cliente visto por el administrador, con los datos para decidir si se puede inactivar.
 */
@Data
public class ClienteAdminRs {

    private Integer idCliente;
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String empresa;
    /** Estado del usuario: ACTIVO o INACTIVO. */
    private String estado;
    private Integer puntaje;
    private Boolean esProspecto;
    private String estadoProspecto;
    private Integer idAsesor;
    private String nombreAsesor;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoIngreso;
    /** Ingresos dentro de la ventana de clientes calificados. */
    private Integer ingresosPeriodo;
    private Boolean tieneLicencia;
    private Integer diasSinIngresar;
    /** true si lleva DIAS_INACTIVIDAD o mas sin ingresar y no tiene licencia. */
    private Boolean inactivable;
}
