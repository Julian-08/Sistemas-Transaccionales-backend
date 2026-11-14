package com.uniminuto.zimbra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Parametros del sistema que el administrador puede cambiar sin reiniciar (RF-017).
 * Claves: PUNTAJE_MINIMO, DIAS_PRUEBA, INGRESOS_CALIFICADO, DIAS_CALIFICADO y DIAS_INACTIVIDAD.
 * Scripts: src/main/resources/db/01_configuracion.sql y 03_configuracion_clientes.sql
 */
@Entity
@Data
@Table(name = "configuracion")
public class Configuracion {

    public static final String PUNTAJE_MINIMO = "PUNTAJE_MINIMO";
    public static final String DIAS_PRUEBA = "DIAS_PRUEBA";
    public static final String INGRESOS_CALIFICADO = "INGRESOS_CALIFICADO";
    public static final String DIAS_CALIFICADO = "DIAS_CALIFICADO";
    public static final String DIAS_INACTIVIDAD = "DIAS_INACTIVIDAD";

    @Id
    @Column(name = "clave", length = 50)
    private String clave;

    @Column(name = "valor", nullable = false, length = 100)
    private String valor;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;
}
