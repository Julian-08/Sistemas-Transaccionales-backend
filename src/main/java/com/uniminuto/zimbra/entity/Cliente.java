package com.uniminuto.zimbra.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Integer idUsuario;

    @Column(name = "empresa", nullable = false, length = 100)
    private String empresa;

    @Column(name = "origen_web", length = 100)
    private String origenWeb;

    @Column(name = "puntaje", nullable = false)
    private Integer puntaje;

    @Column(name = "fecha_registro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "es_prospecto", nullable = false)
    private Boolean esProspecto;

    @Column(name = "estado_prospecto", length = 20)
    private String estadoProspecto;

    @Column(name = "fecha_clasificacion")
    private LocalDateTime fechaClasificacion;

    @Column(name = "id_asesor")
    private Integer idAsesor;
}
