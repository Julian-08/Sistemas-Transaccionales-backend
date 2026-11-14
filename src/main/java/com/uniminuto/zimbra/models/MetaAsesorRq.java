package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Cambiar la meta mensual de ventas de un asesor.
 */
@Data
public class MetaAsesorRq {

    @NotNull(message = "El id del asesor es obligatorio.")
    private Integer idAsesor;

    @NotNull(message = "La meta mensual es obligatoria.")
    @PositiveOrZero(message = "La meta mensual no puede ser negativa.")
    private BigDecimal metaMensual;
}
