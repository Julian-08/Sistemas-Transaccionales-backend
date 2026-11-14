package com.uniminuto.zimbra.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Una opcion del menu del usuario (RF-002).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuOpcionRs {

    private String codigo;
    private String nombre;
    private String ruta;
}
