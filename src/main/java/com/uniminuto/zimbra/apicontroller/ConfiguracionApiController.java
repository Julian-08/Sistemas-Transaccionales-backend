package com.uniminuto.zimbra.apicontroller;

import com.uniminuto.zimbra.api.ConfiguracionApi;
import com.uniminuto.zimbra.entity.Configuracion;
import com.uniminuto.zimbra.models.ConfiguracionRq;
import com.uniminuto.zimbra.models.PuntajeMinimoRq;
import com.uniminuto.zimbra.models.RespuestaRs;
import com.uniminuto.zimbra.service.ConfiguracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ConfiguracionApiController implements ConfiguracionApi {

    private final ConfiguracionService configuracionService;

    public ConfiguracionApiController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @Override
    public ResponseEntity<List<Configuracion>> listar() {
        return ResponseEntity.ok(configuracionService.listar());
    }

    @Override
    public ResponseEntity<Map<String, Integer>> obtenerPuntajeMinimo() {
        return ResponseEntity.ok(Map.of("puntajeMinimo", configuracionService.obtenerPuntajeMinimo()));
    }

    @Override
    public ResponseEntity<RespuestaRs> actualizar(ConfiguracionRq configuracionRq) {
        return ResponseEntity.ok(configuracionService.actualizar(configuracionRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> actualizarPuntajeMinimo(PuntajeMinimoRq puntajeRq) {
        return ResponseEntity.ok(configuracionService.actualizarPuntajeMinimo(puntajeRq));
    }
}
