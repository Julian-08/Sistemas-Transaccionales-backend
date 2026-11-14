package com.uniminuto.zimbra.service;

import com.uniminuto.zimbra.models.MenuOpcionRs;

import java.util.List;

/**
 * Menu de opciones de cada rol (RF-002).
 */
public interface MenuService {

    List<MenuOpcionRs> menuPorRol(String rol);
}
