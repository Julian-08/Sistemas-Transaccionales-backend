package com.zimbra.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "asesor_comercial")
public class AsesorComercial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asesor")
    private Integer idAsesor;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Integer idUsuario;

    @Column(name = "meta_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal metaMensual;
}
