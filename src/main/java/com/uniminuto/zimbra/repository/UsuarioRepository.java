package com.uniminuto.zimbra.repository;

import com.uniminuto.zimbra.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Acceso a la tabla usuarios.
 * La entidad Usuario no expone el hash de la contrasena; por eso se consulta y
 * se actualiza con consultas propias.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmailAndIdUsuarioNot(String email, Integer idUsuario);

    @Query("select u.contrasenaHash from Usuario u where u.idUsuario = :idUsuario")
    Optional<String> buscarContrasenaHash(@Param("idUsuario") Integer idUsuario);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Usuario u set u.nombre = :nombre, u.email = :email where u.idUsuario = :idUsuario")
    int actualizarDatos(@Param("idUsuario") Integer idUsuario,
                        @Param("nombre") String nombre,
                        @Param("email") String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Usuario u set u.contrasenaHash = :hash where u.idUsuario = :idUsuario")
    int actualizarContrasena(@Param("idUsuario") Integer idUsuario, @Param("hash") String hash);
}
