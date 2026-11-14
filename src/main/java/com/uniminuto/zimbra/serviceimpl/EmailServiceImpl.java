package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.service.EmailService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Envia los correos del sistema en segundo plano, en HTML con la marca de Zimbra
 * y con una version en texto plano para los clientes de correo que no muestran HTML.
 *
 * Con zimbra.correo.habilitado=false no envia nada y escribe el enlace en la consola,
 * para poder probar la recuperacion de contrasena sin una cuenta de correo.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String NOMBRE_REMITENTE = "Zimbra CRM";
    private static final String CORREO_DE_EJEMPLO = "tucorreo@gmail.com";
    private static final String MENSAJE_CREDENCIALES_RECHAZADAS =
            "Gmail rechazó el usuario o la contraseña de {}. Revise correo-local.properties: "
            + "spring.mail.password debe ser una CONTRASEÑA DE APLICACIÓN de Google (16 letras, sin espacios), "
            + "no la contraseña normal, y la verificación en dos pasos debe estar activa.";

    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean habilitado;
    private final String remitente;
    private final String usuarioSmtp;
    private final String urlFrontend;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSender,
                            @Value("${zimbra.correo.habilitado:false}") boolean habilitado,
                            @Value("${zimbra.correo.remitente:}") String remitente,
                            @Value("${spring.mail.username:}") String usuarioSmtp,
                            @Value("${zimbra.frontend.url:http://localhost:4200}") String urlFrontend) {
        this.mailSender = mailSender;
        this.habilitado = habilitado;
        this.usuarioSmtp = usuarioSmtp;
        // Gmail exige que el remitente sea la misma cuenta que inicia sesion.
        this.remitente = remitente == null || remitente.isBlank() ? usuarioSmtp : remitente;
        this.urlFrontend = urlFrontend;
    }

    /**
     * Al arrancar revisa la configuracion del correo y prueba la conexion con el servidor SMTP
     * (en un hilo aparte para no demorar el arranque), asi el error se ve de una vez en la consola
     * y no solo cuando alguien pide recuperar la contrasena.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void revisarConfiguracion() {
        if (!habilitado) {
            LOG.info("Correo deshabilitado: los códigos de recuperación se muestran en pantalla y en esta consola. "
                    + "Para enviarlos por correo configure correo-local.properties.");
            return;
        }
        if (usuarioSmtp == null || usuarioSmtp.isBlank()) {
            LOG.warn("zimbra.correo.habilitado=true pero spring.mail.username está vacío: no se podrán enviar correos.");
            return;
        }
        if (usuarioSmtp.equalsIgnoreCase(CORREO_DE_EJEMPLO)) {
            LOG.warn("correo-local.properties todavía tiene el correo de ejemplo ({}). Escriba su cuenta de Gmail "
                    + "y su contraseña de aplicación y reinicie: no se enviará ningún correo.", CORREO_DE_EJEMPLO);
            return;
        }
        LOG.info("Correo habilitado: se enviarán correos desde {}. Probando la conexión con el servidor...", remitente);
        Thread.ofVirtual().name("prueba-smtp").start(this::probarConexion);
    }

    private void probarConexion() {
        if (!(mailSender.getIfAvailable() instanceof JavaMailSenderImpl sender)) {
            return;
        }
        try {
            sender.testConnection();
            LOG.info("Conexión con el servidor de correo correcta: Gmail aceptó la cuenta {}.", usuarioSmtp);
        } catch (AuthenticationFailedException ex) {
            LOG.error(MENSAJE_CREDENCIALES_RECHAZADAS, usuarioSmtp);
        } catch (Exception ex) {
            LOG.error("No se pudo conectar con el servidor de correo: {}", ex.getMessage());
        }
    }

    @Async
    @Override
    public void enviarCodigoRecuperacion(String email, String nombre, String codigo, int minutosVigencia) {
        if (!habilitado) {
            LOG.info("[CORREO DESHABILITADO] Código de recuperación para {}: {} (vence en {} min)",
                    email, codigo, minutosVigencia);
            return;
        }
        String enlace = urlFrontend + "/#/restablecer-contrasena?email="
                + URLEncoder.encode(email, StandardCharsets.UTF_8);
        String cuerpo = """
                <p style="margin:0 0 16px">Hola <strong>%s</strong>,</p>
                <p style="margin:0 0 16px">Recibimos una solicitud para restablecer la contraseña de tu cuenta de Zimbra CRM.
                Este es tu código:</p>
                <p style="margin:0 0 20px;font-size:28px;font-weight:700;letter-spacing:4px;color:#0b5cad">%s</p>
                %s
                <p style="margin:24px 0 8px;color:#66758a;font-size:13px">El código vence en <strong>%d minutos</strong> y solo se puede usar una vez.</p>
                <p style="margin:0;color:#66758a;font-size:13px">Si tú no hiciste esta solicitud, ignora este correo: tu contraseña no cambiará.</p>
                """.formatted(escapar(nombre), escapar(codigo), boton("Escribir el código", enlace), minutosVigencia);
        String texto = "Hola " + nombre + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña de Zimbra CRM.\n"
                + "Tu código es: " + codigo + " (vence en " + minutosVigencia + " minutos).\n\n"
                + "Escríbelo en " + enlace + "\n\n"
                + "Si tú no hiciste la solicitud, ignora este correo.\n\nEquipo Zimbra";
        enviar(email, "Restablece tu contraseña de Zimbra CRM", plantilla("Restablece tu contraseña", cuerpo), texto);
    }

    @Async
    @Override
    public void enviarBienvenida(String email, String nombre, String empresa) {
        if (!habilitado) {
            LOG.info("[CORREO DESHABILITADO] Bienvenida para {}", email);
            return;
        }
        String enlace = urlFrontend + "/#/login";
        String cuerpo = """
                <p style="margin:0 0 16px">Hola <strong>%s</strong>,</p>
                <p style="margin:0 0 16px">Tu cuenta de Zimbra para <strong>%s</strong> ya está lista.
                Ahora puedes activar tu prueba gratuita de 60 días y empezar a trabajar con tu equipo.</p>
                <ul style="margin:0 0 20px;padding-left:20px;color:#16263d">
                  <li style="margin-bottom:6px">Correo empresarial con tu dominio</li>
                  <li style="margin-bottom:6px">Calendarios y contactos compartidos</li>
                  <li>Documentos y tareas en un solo lugar</li>
                </ul>
                %s
                <p style="margin:24px 0 0;color:#66758a;font-size:13px">Un asesor comercial te acompañará si necesitas ayuda.</p>
                """.formatted(escapar(nombre), escapar(empresa), boton("Ingresar a mi cuenta", enlace));
        String texto = "Hola " + nombre + ",\n\nTu cuenta de Zimbra para " + empresa + " ya está lista.\n"
                + "Ingresa en " + enlace + " y activa tu prueba gratuita de 60 días.\n\nEquipo Zimbra";
        enviar(email, "Bienvenido a Zimbra", plantilla("¡Bienvenido a Zimbra!", cuerpo), texto);
    }

    private void enviar(String para, String asunto, String html, String texto) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            LOG.error("No hay servidor de correo configurado; no se envió '{}' a {}", asunto, para);
            return;
        }
        try {
            MimeMessage mensaje = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, StandardCharsets.UTF_8.name());
            helper.setFrom(remitente, NOMBRE_REMITENTE);
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(texto, html);
            sender.send(mensaje);
            LOG.info("Correo '{}' enviado a {}", asunto, para);
            if (para != null && para.toLowerCase().endsWith(".test")) {
                LOG.warn("El destinatario {} usa el dominio de prueba .test: el correo no le llegará a nadie. "
                        + "Para probar use un usuario con un correo real.", para);
            }
        } catch (MailAuthenticationException ex) {
            LOG.error("No se pudo enviar el correo '{}' a {}.", asunto, para);
            LOG.error(MENSAJE_CREDENCIALES_RECHAZADAS, usuarioSmtp);
        } catch (Exception ex) {
            LOG.error("No se pudo enviar el correo '{}' a {}: {}", asunto, para, ex.getMessage());
        }
    }

    /** Estructura comun de los correos: cabecera azul con el logo, contenido y pie. */
    private String plantilla(String titulo, String contenido) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Segoe UI,Arial,sans-serif;color:#16263d">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f7fb;padding:32px 12px">
                    <tr><td align="center">
                      <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                             style="max-width:560px;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #e3e8ef">
                        <tr><td style="background:#0b5cad;padding:24px 32px">
                          <table role="presentation" cellpadding="0" cellspacing="0"><tr>
                            <td style="width:40px;height:40px;background:#1f8bff;border-radius:10px;color:#ffffff;
                                       font-size:22px;font-weight:700;text-align:center;vertical-align:middle">Z</td>
                            <td style="padding-left:12px;color:#ffffff;font-size:18px;font-weight:600">Zimbra CRM</td>
                          </tr></table>
                        </td></tr>
                        <tr><td style="padding:32px">
                          <h1 style="margin:0 0 20px;font-size:22px;color:#16263d">%s</h1>
                          <div style="font-size:15px;line-height:1.6">%s</div>
                        </td></tr>
                        <tr><td style="padding:20px 32px;background:#f7f9fc;border-top:1px solid #e3e8ef;color:#66758a;font-size:12px">
                          Este correo fue enviado automáticamente por Zimbra CRM. Por favor no respondas a este mensaje.
                        </td></tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(escapar(titulo), contenido);
    }

    private String boton(String texto, String enlace) {
        String url = escapar(enlace);
        return """
                <table role="presentation" cellpadding="0" cellspacing="0" style="margin:8px 0">
                  <tr><td style="border-radius:8px;background:#0b5cad">
                    <a href="%s" style="display:inline-block;padding:12px 24px;color:#ffffff;font-weight:600;text-decoration:none">%s</a>
                  </td></tr>
                </table>
                <p style="margin:12px 0 0;color:#66758a;font-size:12px">Si el botón no funciona, copia este enlace en tu navegador:<br>
                <a href="%s" style="color:#0b5cad;word-break:break-all">%s</a></p>
                """.formatted(url, escapar(texto), url, url);
    }

    private String escapar(String valor) {
        return valor == null ? "" : HtmlUtils.htmlEscape(valor, StandardCharsets.UTF_8.name());
    }
}
