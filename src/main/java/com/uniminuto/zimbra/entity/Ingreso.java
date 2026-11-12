package com.zimbra.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ingresos")
public class Ingreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingreso")
    private Integer idIngreso;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaHora;
}
