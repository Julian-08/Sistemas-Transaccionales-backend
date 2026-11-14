package com.uniminuto.zimbra.config;

import com.uniminuto.zimbra.security.AutenticacionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra el control de sesion y de rol en todas las rutas, y el CORS para el frontend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacionInterceptor autenticacionInterceptor;
    private final String[] origenesPermitidos;

    public WebConfig(AutenticacionInterceptor autenticacionInterceptor,
                     @Value("${zimbra.cors.origenes}") String[] origenesPermitidos) {
        this.autenticacionInterceptor = autenticacionInterceptor;
        this.origenesPermitidos = origenesPermitidos;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacionInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origenesPermitidos)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders(AutenticacionInterceptor.HEADER_TOKEN_RENOVADO, "Content-Disposition");
    }
}
