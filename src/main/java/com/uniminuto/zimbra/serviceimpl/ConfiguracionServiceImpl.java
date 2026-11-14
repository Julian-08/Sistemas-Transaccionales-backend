package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.entity.Configuracion;
import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.exception.RecursoNoEncontradoException;
import com.uniminuto.zimbra.models.ConfiguracionRq;
import com.uniminuto.zimbra.models.PuntajeMinimoRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.repository.ConfiguracionRepository;
import com.uniminuto.zimbra.service.ConfiguracionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Parametros del sistema. El trigger trg_clientes_bu lee PUNTAJE_MINIMO en cada
 * actualizacion de puntaje, asi el cambio aplica sin reiniciar (RF-017).
 */
@Service
public class ConfiguracionServiceImpl implements ConfiguracionService {

    /** Parametros numericos que el administrador puede cambiar y su valor por defecto. */
    private static final Map<String, Integer> VALORES_DEFECTO = Map.of(
            Configuracion.PUNTAJE_MINIMO, 50,
            Configuracion.DIAS_PRUEBA, 60,
            Configuracion.INGRESOS_CALIFICADO, 50,
            Configuracion.DIAS_CALIFICADO, 60,
            Configuracion.DIAS_INACTIVIDAD, 60);

    private static final int VALOR_MAXIMO = 100000;

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionServiceImpl(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Override
    public List<Configuracion> listar() {
        return configuracionRepository.findAll();
    }

    @Override
    public int obtenerPuntajeMinimo() {
        return leerEntero(Configuracion.PUNTAJE_MINIMO);
    }

    @Override
    public RespuestaRs actualizarPuntajeMinimo(PuntajeMinimoRq puntajeRq) {
        guardar(Configuracion.PUNTAJE_MINIMO, puntajeRq.getPuntajeMinimo());
        return RespuestaRs.ok("Puntaje mínimo actualizado a " + puntajeRq.getPuntajeMinimo()
                + ". Se aplica a las próximas clasificaciones.");
    }

    @Override
    public RespuestaRs actualizar(ConfiguracionRq configuracionRq) {
        String clave = configuracionRq.getClave().trim().toUpperCase();
        if (!VALORES_DEFECTO.containsKey(clave)) {
            throw new RecursoNoEncontradoException("El parámetro '" + clave + "' no existe.");
        }
        int valor;
        try {
            valor = Integer.parseInt(configuracionRq.getValor().trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El valor debe ser un número entero.");
        }
        if (valor <= 0 || valor > VALOR_MAXIMO) {
            throw new BadRequestException("El valor debe ser un número positivo menor o igual a " + VALOR_MAXIMO + ".");
        }
        guardar(clave, valor);
        return RespuestaRs.ok("Parámetro " + clave + " actualizado a " + valor + ".");
    }

    @Override
    public int obtenerDiasPrueba() {
        return leerEntero(Configuracion.DIAS_PRUEBA);
    }

    @Override
    public int obtenerIngresosCalificado() {
        return leerEntero(Configuracion.INGRESOS_CALIFICADO);
    }

    @Override
    public int obtenerDiasCalificado() {
        return leerEntero(Configuracion.DIAS_CALIFICADO);
    }

    @Override
    public int obtenerDiasInactividad() {
        return leerEntero(Configuracion.DIAS_INACTIVIDAD);
    }

    private void guardar(String clave, int valor) {
        Configuracion configuracion = configuracionRepository.findById(clave)
                .orElseGet(() -> {
                    Configuracion nueva = new Configuracion();
                    nueva.setClave(clave);
                    return nueva;
                });
        configuracion.setValor(String.valueOf(valor));
        configuracionRepository.save(configuracion);
    }

    private int leerEntero(String clave) {
        int valorDefecto = VALORES_DEFECTO.get(clave);
        return configuracionRepository.findById(clave)
                .map(Configuracion::getValor)
                .map(valor -> {
                    try {
                        return Integer.parseInt(valor.trim());
                    } catch (NumberFormatException ex) {
                        return valorDefecto;
                    }
                })
                .orElse(valorDefecto);
    }
}
