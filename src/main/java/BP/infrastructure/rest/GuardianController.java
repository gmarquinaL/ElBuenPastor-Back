package BP.infrastructure.rest;

import BP.application.dto.GuardianDTO;
import BP.application.service.IGuardianService;
import BP.application.util.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guardians")
public class GuardianController {

    private final IGuardianService guardianService;

    public GuardianController(IGuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<GuardianDTO>> addGuardian(@RequestBody GuardianDTO guardianDTO) throws Exception {
        return guardianService.saveGuardian(guardianDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<GuardianDTO>>> getAllGuardians() throws Exception {
        return guardianService.findAllGuardians();
    }

    /*metodo solo para listar nombre*/

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericResponse<GuardianDTO>> updateGuardian(@PathVariable Integer id, @RequestBody GuardianDTO guardianDTO) throws Exception {
        guardianDTO.setId(id);
        return guardianService.updateGuardian(guardianDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteGuardian(@PathVariable Integer id) throws Exception {
        return guardianService.deleteGuardian(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuardianDTO>> searchGuardians(@RequestParam String query) {
        List<GuardianDTO> guardians = guardianService.searchGuardians(query);
        return ResponseEntity.ok(guardians);
    }

}
