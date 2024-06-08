package BP.infrastructure.rest;

import BP.application.service.impl.EquipoService;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/equipo")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @PostMapping("/agregar")
    public BestGenericResponse<Equipment> addEquipo(@RequestBody Equipment equipo) {
        return equipoService.addEquipo(equipo);
    }

    @PutMapping("/modificar")
    public BestGenericResponse<Equipment> updateEquipo(@RequestBody Equipment equipo) {
        return equipoService.updateEquipo(equipo);
    }

    @DeleteMapping("/eliminar/{id}")
    public BestGenericResponse<Void> deleteEquipo(@PathVariable Integer id) {
        return equipoService.deleteEquipo(id);
    }

    @GetMapping("/listar")
    public BestGenericResponse<List<Equipment>> listAllEquipos() {
        return equipoService.findAllEquipos();
    }

    @PostMapping("/escanearCodigoBarra")
    public ResponseEntity<BestGenericResponse<Equipment>> escanearCodigoBarra(@RequestParam("file") MultipartFile file) {
        BestGenericResponse<Equipment> response = equipoService.scanAndCopyBarcodeData(file);
        return response.getRpta() == Global.RPTA_OK ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/descargarReporte")
    public ResponseEntity<byte[]> downloadExcelReport() {
        try {
            byte[] bytes = equipoService.generateExcelReport();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_De_Equipos.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BestGenericResponse<Equipment>> getEquipoById(@PathVariable int id) {
        BestGenericResponse<Equipment> response = equipoService.getEquipoById(id);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 404).body(response);
    }

    @GetMapping("/filtro/nombre")
    public ResponseEntity<BestGenericResponse<List<Equipment>>> filtroPorNombre(@RequestParam String nombreEquipo) {
        return ResponseEntity.ok(equipoService.filtroPorNombre(nombreEquipo));
    }

    @GetMapping("/filtro/codigoPatrimonial")
    public ResponseEntity<BestGenericResponse<List<Equipment>>> filtroCodigoPatrimonial(@RequestParam String codigoPatrimonial) {
        return ResponseEntity.ok(equipoService.filtroCodigoPatrimonial(codigoPatrimonial));
    }

    @GetMapping("/filtro/fechaCompra")
    public ResponseEntity<BestGenericResponse<List<Equipment>>> filtroFechaCompraBetween(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFin) {
        return ResponseEntity.ok(equipoService.filtroFechaCompraBetween(fechaInicio, fechaFin));
    }

    @GetMapping("/generarCodigoBarra/{codigoPatrimonial}")
    public ResponseEntity<byte[]> generarCodigoBarra(@PathVariable String codigoPatrimonial) {
        BestGenericResponse<byte[]> response = equipoService.generateBarcodeImageForPatrimonialCode(codigoPatrimonial);
        if (response.getRpta() == Global.RPTA_OK) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(response.getBody());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
