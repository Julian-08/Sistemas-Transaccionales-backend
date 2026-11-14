package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Cierre de venta de licencias sobre una propuesta.
 */
@Data
public class VentaRq {

    @NotNull(message = "El id de la propuesta es obligatorio.")
    private Integer idPropuesta;

    @NotNull(message = "El monto final es obligatorio.")
    @Positive(message = "El monto final debe ser mayor que cero.")
    @Digits(integer = 10, fraction = 2, message = "El monto final tiene un formato no válido.")
    private BigDecimal montoFinal;

    @NotBlank(message = "El tipo de licencia es obligatorio.")
    @Pattern(regexp = "ILIMITADA|PAGO_POR_USO", message = "El tipo de licencia debe ser ILIMITADA o PAGO_POR_USO.")
    private String tipoLicencia;

    @NotBlank(message = "El periodo es obligatorio.")
    @Pattern(regexp = "MENSUAL|TRIMESTRAL|SEMESTRAL|ANUAL",
            message = "El periodo debe ser MENSUAL, TRIMESTRAL, SEMESTRAL o ANUAL.")
    private String periodo;
}
