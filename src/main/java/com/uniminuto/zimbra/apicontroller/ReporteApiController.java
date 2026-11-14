package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.ReporteApi;
import com.uniminuto.zimbra.models.ReporteRs;
import com.uniminuto.zimbra.service.ReporteService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
public class ReporteApiController implements ReporteApi {

    /** BOM UTF-8 para que Excel muestre bien las tildes del CSV. */
    private static final String BOM = "﻿";

    private final ReporteService reporteService;

    public ReporteApiController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @Override
    public ResponseEntity<java.util.Map<String, Object>> resumen() {
        return ResponseEntity.ok(reporteService.resumenAdministrador());
    }

    @Override
    public ResponseEntity<ReporteRs> generar(String tipo, LocalDate desde, LocalDate hasta) {
        return ResponseEntity.ok(reporteService.generar(tipo, desde, hasta));
    }

    @Override
    public ResponseEntity<byte[]> exportar(String tipo, LocalDate desde, LocalDate hasta) {
        ReporteRs reporte = reporteService.generar(tipo, desde, hasta);
        byte[] contenido = (BOM + reporteService.aCsv(reporte)).getBytes(StandardCharsets.UTF_8);
        String nombreArchivo = "reporte-" + reporte.getTipo() + "-" + reporte.getDesde() + "_" + reporte.getHasta() + ".csv";
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombreArchivo).build().toString())
                .body(contenido);
    }
}
