package BP.infrastructure.rest.App;


import BP.application.service.impl.App.UbicacionService;
import BP.application.util.BestGenericResponse;
import BP.domain.entity.App.Location;
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
    private UbicacionService ubicacionService;

    @GetMapping("/listar")
    public ResponseEntity<BestGenericResponse<List<Location>>> listarTodasLasUbicaciones() {
        BestGenericResponse<List<Location>> response = ubicacionService.listarTodasLasUbicaciones();
        return ResponseEntity.ok(response);
    }
}
