package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.PropuestaRfp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla propuestas_rfp.
 */
@Repository
public interface PropuestaRfpRepository extends JpaRepository<PropuestaRfp, Integer> {
}
