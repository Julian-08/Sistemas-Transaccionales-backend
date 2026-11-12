package com.zimbra.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pruebas_descarga")
public class PruebaDescarga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prueba")
    private Integer idPrueba;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "fecha_inicio", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "dias_prueba", nullable = false)
    private Integer diasPrueba;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;
}
