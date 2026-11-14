package com.uniminuto.zimbra.security;

/**
 * Roles del sistema. Los id coinciden con la tabla roles y con los
 * procedimientos almacenados (1 = administrador, 2 = asesor, 3 = cliente).
 */
public final class Roles {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ASESOR = "ASESOR";
    public static final String CLIENTE = "CLIENTE";

    public static final int ID_ADMINISTRADOR = 1;
    public static final int ID_ASESOR = 2;
    public static final int ID_CLIENTE = 3;

    private Roles() {
    }

    /** Devuelve el nombre del rol a partir de su id, o null si no existe. */
    public static String nombrePorId(Integer idRol) {
        if (idRol == null) {
            return null;
        }
        return switch (idRol) {
            case ID_ADMINISTRADOR -> ADMINISTRADOR;
            case ID_ASESOR -> ASESOR;
            case ID_CLIENTE -> CLIENTE;
            default -> null;
        };
    }
}
