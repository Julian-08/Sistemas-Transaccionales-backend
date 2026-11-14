package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Acceso a la tabla clientes.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByIdUsuario(Integer idUsuario);
}
