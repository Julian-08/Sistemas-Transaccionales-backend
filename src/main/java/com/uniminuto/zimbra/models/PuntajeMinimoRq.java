package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Nuevo puntaje minimo para clasificar a un cliente como prospecto (RF-017).
 */
@Data
public class PuntajeMinimoRq {

    @NotNull(message = "El puntaje mínimo es obligatorio.")
    @Positive(message = "El puntaje mínimo debe ser un número positivo.")
    @Max(value = 100000, message = "El puntaje mínimo es demasiado alto.")
    private Integer puntajeMinimo;
}
