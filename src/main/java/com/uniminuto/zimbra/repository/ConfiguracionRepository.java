package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla configuracion (RF-017).
 */
@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {
}
