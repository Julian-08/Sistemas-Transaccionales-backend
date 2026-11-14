package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.models.MenuOpcionRs;
import com.uniminuto.zimbra.security.Roles;
import com.uniminuto.zimbra.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Menu de cada rol (RF-002). Las rutas coinciden con las del frontend Angular
 * y cada opcion usa endpoints que el AutenticacionInterceptor solo deja usar a ese rol (RNF-002).
 */
@Service
public class MenuServiceImpl implements MenuService {

    private static final Map<String, List<MenuOpcionRs>> MENUS = Map.of(
            Roles.ADMINISTRADOR, List.of(
                    new MenuOpcionRs("PANEL", "Panel general", "/inicio/admin/panel"),
                    new MenuOpcionRs("USUARIOS", "Usuarios y asesores", "/inicio/admin/usuarios"),
                    new MenuOpcionRs("ASESORES", "Metas de asesores", "/inicio/admin/asesores"),
                    new MenuOpcionRs("CLIENTES", "Clientes", "/inicio/admin/clientes"),
                    new MenuOpcionRs("CALIFICADOS", "Listos para plan de pago", "/inicio/admin/calificados"),
                    new MenuOpcionRs("PROSPECTOS", "Prospectos y asignación", "/inicio/admin/prospectos"),
                    new MenuOpcionRs("SEGUIMIENTOS", "Seguimientos", "/inicio/admin/seguimientos"),
                    new MenuOpcionRs("VENTAS", "Ventas de licencias", "/inicio/admin/ventas"),
                    new MenuOpcionRs("ALERTAS", "Alertas", "/inicio/alertas"),
                    new MenuOpcionRs("REPORTES", "Reportes", "/inicio/admin/reportes"),
                    new MenuOpcionRs("CONFIGURACION", "Configuración", "/inicio/admin/configuracion")),
            Roles.ASESOR, List.of(
                    new MenuOpcionRs("PANEL", "Mis estadísticas", "/inicio/asesor/panel"),
                    new MenuOpcionRs("CALIFICADOS", "Listos para plan de pago", "/inicio/asesor/calificados"),
                    new MenuOpcionRs("MIS_PROSPECTOS", "Mis prospectos", "/inicio/asesor/prospectos"),
                    new MenuOpcionRs("ALERTAS", "Alertas", "/inicio/alertas"),
                    new MenuOpcionRs("PROPUESTAS", "Propuestas", "/inicio/asesor/propuestas"),
                    new MenuOpcionRs("VENTAS", "Mis ventas", "/inicio/asesor/ventas")),
            Roles.CLIENTE, List.of(
                    new MenuOpcionRs("MI_CUENTA", "Mi cuenta y prueba", "/inicio/cliente/mi-cuenta"),
                    new MenuOpcionRs("MI_EMPRESA", "Zimbra en mi empresa", "/inicio/cliente/mi-empresa"),
                    new MenuOpcionRs("PLANES", "Planes y licencias", "/inicio/cliente/planes"))
    );

    @Override
    public List<MenuOpcionRs> menuPorRol(String rol) {
        return MENUS.getOrDefault(rol, List.of());
    }
}
