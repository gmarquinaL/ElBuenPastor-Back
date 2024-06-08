package BP.infrastructure.rest;


import BP.application.service.impl.AdministrativoService;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.Administrative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administrativo")
public class AdministrativoController {

    @Autowired
    private AdministrativoService administrativoService;

    @PostMapping("/agregar")
    public ResponseEntity<BestGenericResponse<Administrative>> agregarAdministrativo(@RequestBody Administrative administrativo) {
        BestGenericResponse<Administrative> response = administrativoService.agregarAdministrativo(administrativo);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<BestGenericResponse<Administrative>> editarAdministrativo(@PathVariable Integer id, @RequestBody Administrative administrativo) {
        administrativo.setId(id);
        BestGenericResponse<Administrative> response = administrativoService.editarAdministrativo(administrativo);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<BestGenericResponse<Void>> eliminarAdministrativo(@PathVariable Integer id) {
        BestGenericResponse<Void> response = administrativoService.eliminarAdministrativo(id);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<BestGenericResponse<List<Administrative>>> listarTodosLosAdministrativos() {
        BestGenericResponse<List<Administrative>> response = administrativoService.listarTodosLosAdministrativos();
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }
}
