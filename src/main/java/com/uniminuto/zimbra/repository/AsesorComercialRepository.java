package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.AsesorComercial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Acceso a la tabla asesor_comercial.
 */
@Repository
public interface AsesorComercialRepository extends JpaRepository<AsesorComercial, Integer> {

    Optional<AsesorComercial> findByIdUsuario(Integer idUsuario);
}
