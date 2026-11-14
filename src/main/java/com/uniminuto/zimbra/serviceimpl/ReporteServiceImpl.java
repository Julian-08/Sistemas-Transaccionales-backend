package com.uniminuto.zimbra.serviceimpl;

import com.uniminuto.zimbra.exception.BadRequestException;
import com.uniminuto.zimbra.models.ReporteRs;
import com.uniminuto.zimbra.repository.ReporteRepository;
import com.uniminuto.zimbra.service.ConfiguracionService;
import com.uniminuto.zimbra.service.ReporteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    public static final String VISITAS = "visitas";
    public static final String PROSPECTOS = "prospectos";
    public static final String CONVERSION = "conversion";
    public static final String DESEMPENO_ASESOR = "desempeno-asesor";

    private static final int DIAS_POR_DEFECTO = 30;
    private static final String SEPARADOR = ";";

    private final ReporteRepository reporteRepository;
    private final ConfiguracionService configuracionService;

    public ReporteServiceImpl(ReporteRepository reporteRepository, ConfiguracionService configuracionService) {
        this.reporteRepository = reporteRepository;
        this.configuracionService = configuracionService;
    }

    @Override
    public ReporteRs generar(String tipo, LocalDate desde, LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(DIAS_POR_DEFECTO);
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha 'desde' no puede ser mayor que la fecha 'hasta'.");
        }
        LocalDateTime inicioRango = inicio.atStartOfDay();
        LocalDateTime finRango = fin.plusDays(1).atStartOfDay();

        List<Map<String, Object>> filas = switch (tipo) {
            case VISITAS -> reporteRepository.visitas(inicioRango, finRango);
            case PROSPECTOS -> reporteRepository.prospectos(inicioRango, finRango);
            case CONVERSION -> reporteRepository.conversion(inicioRango, finRango);
            case DESEMPENO_ASESOR -> reporteRepository.desempenoAsesor(inicioRango, finRango);
            default -> throw new BadRequestException(
                    "Tipo de reporte no válido. Use: visitas, prospectos, conversion o desempeno-asesor.");
        };

        ReporteRs reporte = new ReporteRs();
        reporte.setTipo(tipo);
        reporte.setDesde(inicio);
        reporte.setHasta(fin);
        reporte.setFilas(filas);
        reporte.setColumnas(filas.isEmpty() ? List.of() : new ArrayList<>(filas.get(0).keySet()));
        if (sinDatos(filas)) {
            reporte.setMensaje("No hay datos en el rango de fechas seleccionado.");
        }
        return reporte;
    }

    @Override
    public Map<String, Object> resumenAdministrador() {
        Map<String, Object> resumen = new java.util.LinkedHashMap<>(reporteRepository.resumenAdministrador(
                configuracionService.obtenerIngresosCalificado(),
                configuracionService.obtenerDiasCalificado(),
                LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        resumen.put("ingresos_calificado", configuracionService.obtenerIngresosCalificado());
        resumen.put("dias_calificado", configuracionService.obtenerDiasCalificado());
        return resumen;
    }

    @Override
    public String aCsv(ReporteRs reporte) {
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(SEPARADOR, reporte.getColumnas())).append("\r\n");
        for (Map<String, Object> fila : reporte.getFilas()) {
            csv.append(reporte.getColumnas().stream()
                    .map(columna -> celda(fila.get(columna)))
                    .collect(Collectors.joining(SEPARADOR)));
            csv.append("\r\n");
        }
        return csv.toString();
    }

    /** Sin filas, o una sola fila con todos los valores en cero (reporte de conversion). */
    private boolean sinDatos(List<Map<String, Object>> filas) {
        if (filas.isEmpty()) {
            return true;
        }
        return filas.size() == 1 && filas.get(0).values().stream()
                .allMatch(valor -> valor == null
                        || (valor instanceof Number numero && new BigDecimal(numero.toString()).signum() == 0));
    }

    private String celda(Object valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.toString();
        if (texto.contains(SEPARADOR) || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
