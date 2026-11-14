package com.uniminuto.zimbra.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "actividad_marketing")
public class ActividadMarketing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer idActividad;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(name = "url_visitada", length = 255)
    private String urlVisitada;

    @Column(name = "tiempo_permanencia_seg", nullable = false)
    private Integer tiempoPermanenciaSeg;

    @Column(name = "scoring_interes", nullable = false)
    private Integer scoringInteres;
}
