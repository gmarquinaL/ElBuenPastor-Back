package BP.infrastructure.rest;


import BP.application.dto.TeacherPaymentDTO;
import BP.application.service.impl.PagosService;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.TeacherPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PagosController {

    @Autowired
    private PagosService paymentService;

    @PostMapping(value = "/agregar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BestGenericResponse<TeacherPayment>> agregarPago(@RequestBody TeacherPayment pago) {
        BestGenericResponse<TeacherPayment> response = paymentService.agregarPago(pago);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @PutMapping(value = "/editar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BestGenericResponse<TeacherPayment>> editarPago(@RequestBody TeacherPayment pago) {
        BestGenericResponse<TeacherPayment> response = paymentService.editarPago(pago);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<BestGenericResponse<Void>> eliminarPago(@PathVariable Integer id) {
        BestGenericResponse<Void> response = paymentService.eliminarPago(id);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<BestGenericResponse<List<TeacherPaymentDTO>>> listarTodosLosPagos() {
        BestGenericResponse<List<TeacherPaymentDTO>> response = paymentService.listarTodosLosPagos();
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    @GetMapping("/baucher/{paymentId}")
    public ResponseEntity<byte[]> generarBaucherPdf(@PathVariable int paymentId) {
        try {
            TeacherPayment pago = paymentService.obtenerPagoPorId(paymentId).getBody();
            byte[] pdfContent = paymentService.generarBaucherPdf(pago);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reporte")
    public ResponseEntity<byte[]> generateExcelReport() {
        try {
            byte[] excelContent = paymentService.generateExcelReport();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
