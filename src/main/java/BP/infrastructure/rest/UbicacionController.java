package BP.infrastructure.rest;


import BP.application.service.impl.UbicacionServiceImpl;
import BP.application.util.BestGenericResponse;
import BP.domain.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionServiceImpl ubicacionServiceImpl;

    @GetMapping("/listar")
    public ResponseEntity<BestGenericResponse<List<Location>>> listarTodasLasUbicaciones() {
        BestGenericResponse<List<Location>> response = ubicacionServiceImpl.listarTodasLasUbicaciones();
        return ResponseEntity.ok(response);
    }
}
