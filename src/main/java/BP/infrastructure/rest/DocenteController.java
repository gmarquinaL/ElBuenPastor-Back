package BP.infrastructure.rest;


import BP.application.service.impl.DocenteService;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @PostMapping("/agregar")
    public ResponseEntity<BestGenericResponse<Teacher>> agregarDocente(@RequestBody Teacher docente) {
        BestGenericResponse<Teacher> response = docenteService.agregarDocente(docente);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<BestGenericResponse<Teacher>> editarDocente(@PathVariable Integer id, @RequestBody Teacher docente) {
        docente.setId(id);
        BestGenericResponse<Teacher> response = docenteService.editarDocente(docente);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<BestGenericResponse<Void>> eliminarDocente(@PathVariable Integer id) {
        BestGenericResponse<Void> response = docenteService.eliminarDocente(id);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<BestGenericResponse<List<Teacher>>> listarTodosLosDocentes() {
        BestGenericResponse<List<Teacher>> response = docenteService.listarTodosLosDocentes();
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @GetMapping("/detalles/{id}")
    public ResponseEntity<BestGenericResponse<Teacher>> obtenerDocentePorId(@PathVariable Integer id) {
        BestGenericResponse<Teacher> response = docenteService.obtenerDocentePorId(id);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 404).body(response);
    }
}
