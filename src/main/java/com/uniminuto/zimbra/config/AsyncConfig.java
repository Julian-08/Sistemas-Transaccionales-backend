package com.uniminuto.zimbra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita tareas en segundo plano: el registro de ingresos y de navegacion
 * no debe frenar al cliente (RNF-006).
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
