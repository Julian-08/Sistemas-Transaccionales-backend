package com.zimbra.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "alertas")
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "id_usuario_destino", nullable = false)
    private Integer idUsuarioDestino;

    @Column(name = "mensaje", nullable = false, length = 255)
    private String mensaje;

    @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(name = "leida", nullable = false)
    private Boolean leida;
}
