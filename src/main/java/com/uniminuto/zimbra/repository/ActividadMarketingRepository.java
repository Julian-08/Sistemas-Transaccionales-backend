package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.ActividadMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a la tabla actividad_marketing (navegacion y eventos del cliente).
 */
@Repository
public interface ActividadMarketingRepository extends JpaRepository<ActividadMarketing, Integer> {

    /** Ultimas 100 actividades del cliente, de la mas reciente a la mas antigua (RF-010). */
    List<ActividadMarketing> findTop100ByIdClienteOrderByFechaHoraDesc(Integer idCliente);
}
