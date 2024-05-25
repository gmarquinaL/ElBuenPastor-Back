package BP.infrastructure.rest;

import BP.application.dto.PaymentDTO;
import BP.application.service.impl.PaymentServiceImpl;
import BP.application.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentRestController {
    @Autowired
    private PaymentServiceImpl paymentService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/upload")
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return paymentService.storeFile(file);
    }


    @GetMapping("/byName")
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> getByName(@RequestParam String name) {
        return paymentService.findByName(name);
    }

    @GetMapping("/byCode")
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> getByCode(@RequestParam String code) {
        return paymentService.findByCode(code);
    }

    @GetMapping("/byConcept")
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> getByConcept(@RequestParam String concept) {
        return paymentService.findByConcept(concept);
    }

    @GetMapping("/byPaymentDate")
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> getByPaymentDate(@RequestParam String paymentDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return paymentService.findByPaymentDate(dateTime);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "Incorrect date format. Use yyyy-MM-dd HH:mm:ss", null));
        }
    }

    @GetMapping("/listAll")
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> listAll() {
        return paymentService.findAllPaymentsDTO();
    }

    @GetMapping("/distinct/codes")
    public ResponseEntity<GenericResponse<List<String>>> getDistinctCodes() {
        return paymentService.findAllDistinctCodesDTO();
    }

    @GetMapping("/distinct/concepts")
    public ResponseEntity<GenericResponse<List<String>>> getDistinctConcepts() {
        return paymentService.findAllDistinctConceptsDTO();
    }

    @GetMapping("/distinct/paymentDates")
    public ResponseEntity<GenericResponse<List<LocalDateTime>>> getDistinctPaymentDates() {
        return paymentService.findAllDistinctPaymentDatesDTO();
    }

    @GetMapping("/distinct/names")
    public ResponseEntity<GenericResponse<List<String>>> getDistinctNames() {
        return paymentService.findAllDistinctNamesDTO();
    }

}
