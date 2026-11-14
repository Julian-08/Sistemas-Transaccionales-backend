package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.repository.ProcedimientoRepository;
import com.uniminuto.zimbra.service.RegistroActividadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Guarda ingresos y navegacion en segundo plano (RNF-006): el cliente no espera
 * a que termine y un fallo no interrumpe su navegacion. Si falla por un problema
 * temporal, se reintenta hasta 3 veces (RF-004 A1).
 */
@Service
public class RegistroActividadServiceImpl implements RegistroActividadService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistroActividadServiceImpl.class);
    private static final int INTENTOS = 3;
    private static final long ESPERA_MS = 300;

    private final ProcedimientoRepository procedimientoRepository;

    public RegistroActividadServiceImpl(ProcedimientoRepository procedimientoRepository) {
        this.procedimientoRepository = procedimientoRepository;
    }

    @Async
    @Override
    public void registrarIngreso(Integer idUsuario) {
        ejecutarConReintento("ingreso del usuario " + idUsuario,
                () -> procedimientoRepository.registrarIngreso(idUsuario));
    }

    @Async
    @Override
    public void registrarActividad(Integer idCliente, String tipoEvento, String url, Integer tiempoSeg) {
        ejecutarConReintento("actividad " + tipoEvento + " del cliente " + idCliente,
                () -> procedimientoRepository.registrarActividad(idCliente, tipoEvento, url, tiempoSeg));
    }

    private void ejecutarConReintento(String descripcion, Runnable tarea) {
        for (int intento = 1; intento <= INTENTOS; intento++) {
            try {
                tarea.run();
                return;
            } catch (DataAccessException ex) {
                if (esReglaDeNegocio(ex)) {
                    LOG.warn("No se registró {}: {}", descripcion, ex.getMostSpecificCause().getMessage());
                    return;
                }
                LOG.warn("Intento {}/{} fallido al registrar {}", intento, INTENTOS, descripcion, ex);
                esperar(intento);
            }
        }
        LOG.error("No se pudo registrar {} después de {} intentos", descripcion, INTENTOS);
    }

    private boolean esReglaDeNegocio(DataAccessException ex) {
        return ex.getMostSpecificCause() instanceof SQLException sql && "45000".equals(sql.getSQLState());
    }

    private void esperar(int intento) {
        try {
            Thread.sleep(ESPERA_MS * intento);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
