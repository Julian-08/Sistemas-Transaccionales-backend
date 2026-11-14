package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla seguimientos.
 */
@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Integer> {
}
