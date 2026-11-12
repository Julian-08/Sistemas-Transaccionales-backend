package com.zimbra.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "propuestas_rfp")
public class PropuestaRfp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propuesta")
    private Integer idPropuesta;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "id_asesor", nullable = false)
    private Integer idAsesor;

    @Column(name = "monto_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoEstimado;

    @Column(name = "fecha_emision", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
