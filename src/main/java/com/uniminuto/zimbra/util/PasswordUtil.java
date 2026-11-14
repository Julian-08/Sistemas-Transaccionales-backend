package com.uniminuto.zimbra.util;

import com.uniminuto.zimbra.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Utilidades de contrasenas (RNF-001).
 * <ul>
 *   <li>Las contrasenas se guardan con BCrypt: hash con sal que no se puede revertir.</li>
 *   <li>Los tokens de recuperacion (RF-003) se guardan como SHA-256; el token en claro
 *       solo viaja en el enlace del correo.</li>
 * </ul>
 */
@Component
public class PasswordUtil {

    private static final int LONGITUD_MINIMA = 8;
    /** BCrypt solo usa los primeros 72 bytes de la contrasena. */
    private static final int LONGITUD_MAXIMA = 72;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PasswordEncoder passwordEncoder;

    public PasswordUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /** Cifra la contrasena con BCrypt. */
    public String cifrar(String contrasena) {
        return passwordEncoder.encode(contrasena);
    }

    /** Compara la contrasena digitada con el hash guardado. */
    public boolean coincide(String contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null || hashGuardado.isBlank()) {
            return false;
        }
        try {
            return passwordEncoder.matches(contrasena, hashGuardado);
        } catch (IllegalArgumentException ex) {
            // Hash con formato invalido en la base: se trata como contrasena incorrecta.
            return false;
        }
    }

    /** Valida que la contrasena tenga minimo 8 caracteres, letras y numeros. */
    public void validarFortaleza(String contrasena) {
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA
                || !contrasena.matches(".*[A-Za-z].*") || !contrasena.matches(".*\\d.*")) {
            throw new BadRequestException(
                    "La contraseña debe tener mínimo 8 caracteres e incluir letras y números.");
        }
        if (contrasena.getBytes(StandardCharsets.UTF_8).length > LONGITUD_MAXIMA) {
            throw new BadRequestException("La contraseña no puede superar 72 caracteres.");
        }
    }

    /** Genera un token aleatorio y seguro para el enlace de recuperacion. */
    public String generarTokenRecuperacion() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Hash SHA-256 del token de recuperacion (es lo que se guarda en la base). */
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
