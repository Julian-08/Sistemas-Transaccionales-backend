package com.zimbra.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "seguimientos")
public class Seguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Integer idSeguimiento;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "id_asesor", nullable = false)
    private Integer idAsesor;

    @Column(name = "fecha_seguimiento", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaSeguimiento;

    @Column(name = "medio", nullable = false, length = 20)
    private String medio;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
