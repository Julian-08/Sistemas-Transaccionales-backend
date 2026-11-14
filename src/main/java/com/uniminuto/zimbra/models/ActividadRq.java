package com.uniminuto.zimbra.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Evento de navegacion del cliente enviado por el sitio web (RF-005).
 * La descarga de la prueba se registra en su propio endpoint.
 */
@Data
public class ActividadRq {

    @NotBlank(message = "El tipo de evento es obligatorio.")
    @Pattern(regexp = "VISITA_WEB|EMAIL|FORMULARIO",
            message = "El tipo de evento debe ser VISITA_WEB, EMAIL o FORMULARIO.")
    private String tipoEvento;

    @Size(max = 255, message = "La URL no puede superar 255 caracteres.")
    private String urlVisitada;

    @NotNull(message = "El tiempo de permanencia es obligatorio.")
    @PositiveOrZero(message = "El tiempo de permanencia no puede ser negativo.")
    private Integer tiempoPermanenciaSeg;
}
