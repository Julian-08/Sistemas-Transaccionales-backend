package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Propuesta comercial (RFP) que el asesor envia a su prospecto.
 */
@Data
public class PropuestaRq {

    @NotNull(message = "El id del cliente es obligatorio.")
    private Integer idCliente;

    @NotNull(message = "El monto estimado es obligatorio.")
    @Positive(message = "El monto estimado debe ser mayor que cero.")
    @Digits(integer = 10, fraction = 2, message = "El monto estimado tiene un formato no válido.")
    private BigDecimal montoEstimado;
}
