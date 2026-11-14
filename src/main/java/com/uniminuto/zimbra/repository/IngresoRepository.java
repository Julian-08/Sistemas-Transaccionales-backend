package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a la tabla ingresos (cada inicio de sesion, RF-004).
 */
@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Integer> {

    List<Ingreso> findTop50ByIdUsuarioOrderByFechaHoraDesc(Integer idUsuario);

    long countByIdUsuario(Integer idUsuario);
}
