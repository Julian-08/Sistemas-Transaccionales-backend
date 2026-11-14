package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla roles.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}
