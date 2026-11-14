package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.VentaLicencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla ventas_licencias.
 */
@Repository
public interface VentaLicenciaRepository extends JpaRepository<VentaLicencia, Integer> {

    boolean existsByIdPropuesta(Integer idPropuesta);
}
