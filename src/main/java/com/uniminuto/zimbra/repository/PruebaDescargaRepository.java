package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.PruebaDescarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a la tabla pruebas_descarga.
 */
@Repository
public interface PruebaDescargaRepository extends JpaRepository<PruebaDescarga, Integer> {

    List<PruebaDescarga> findByIdClienteOrderByFechaInicioDesc(Integer idCliente);
}
