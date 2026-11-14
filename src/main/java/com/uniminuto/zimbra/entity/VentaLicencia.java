package com.uniminuto.zimbra.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ventas_licencias")
public class VentaLicencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_propuesta", nullable = false, unique = true)
    private Integer idPropuesta;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "id_asesor", nullable = false)
    private Integer idAsesor;

    @Column(name = "monto_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "fecha_cierre", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCierre;

    @Column(name = "tipo_licencia", nullable = false, length = 30)
    private String tipoLicencia;

    @Column(name = "periodo", nullable = false, length = 20)
    private String periodo;
}
