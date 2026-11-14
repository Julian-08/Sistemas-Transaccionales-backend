package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla alertas.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    long countByIdUsuarioDestinoAndLeidaFalse(Integer idUsuarioDestino);
}
