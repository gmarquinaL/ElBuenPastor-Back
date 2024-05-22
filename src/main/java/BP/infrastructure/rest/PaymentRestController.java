package BP.infrastructure.rest;

import BP.application.service.impl.PaymentServiceImpl;
import BP.application.util.GenericResponse;
import BP.domain.entity.Payment;
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
@RequestMapping("/api/payments")

public class PaymentRestController {
    @Autowired
    private PaymentServiceImpl paymentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            List<Payment> payments = paymentService.storeFile(file);
            return ResponseEntity.ok(new GenericResponse<>("result", 1, "File uploaded and data processed successfully.", payments));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("result", -1, "Error processing the file: " + e.getMessage(), null));
        }
    }

    // Define a date format
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @GetMapping("/byName")
    public ResponseEntity<GenericResponse<List<Payment>>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(paymentService.findByName(name));
    }

    @GetMapping("/byCode")
    public ResponseEntity<GenericResponse<List<Payment>>> getByCode(@RequestParam String code) {
        return ResponseEntity.ok(paymentService.findByCode(code));
    }

    @GetMapping("/byConcept")
    public ResponseEntity<GenericResponse<List<Payment>>> getByConcept(@RequestParam String concept) {
        return ResponseEntity.ok(paymentService.findByConcept(concept));
    }

    @GetMapping("/byPaymentDate")
    public ResponseEntity<GenericResponse<List<Payment>>> getByPaymentDate(@RequestParam String paymentDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return ResponseEntity.ok(paymentService.findByPaymentDate(dateTime));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "Incorrect date format. Use yyyy-MM-dd HH:mm:ss", null));
        }
    }

    @GetMapping("/byNameCode")
    public ResponseEntity<GenericResponse<List<Payment>>> getByNameCode(@RequestParam String name, @RequestParam String code) {
        return ResponseEntity.ok(paymentService.findByNameAndCode(name, code));
    }

    @GetMapping("/byNameCodeConcept")
    public ResponseEntity<GenericResponse<List<Payment>>> getByNameCodeConcept(@RequestParam String name, @RequestParam String code, @RequestParam String concept) {
        return ResponseEntity.ok(paymentService.findByNameCodeAndConcept(name, code, concept));
    }

    @GetMapping("/byNameCodeConceptPaymentDate")
    public ResponseEntity<GenericResponse<List<Payment>>> getByNameCodeConceptPaymentDate(@RequestParam String name, @RequestParam String code, @RequestParam String concept, @RequestParam String paymentDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return ResponseEntity.ok(paymentService.findByNameCodeConceptAndPaymentDate(name, code, concept, dateTime));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "Incorrect date format. Use yyyy-MM-dd HH:mm:ss", null));
        }
    }

    @GetMapping("/byCodeConcept")
    public ResponseEntity<GenericResponse<List<Payment>>> getByCodeConcept(@RequestParam String code, @RequestParam String concept) {
        return ResponseEntity.ok(paymentService.findByCodeAndConcept(code, concept));
    }

    @GetMapping("/byConceptPaymentDate")
    public ResponseEntity<GenericResponse<List<Payment>>> getByConceptPaymentDate(@RequestParam String concept, @RequestParam String paymentDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return ResponseEntity.ok(paymentService.findByConceptAndPaymentDate(concept, dateTime));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "Incorrect date format. Use yyyy-MM-dd HH:mm:ss", null));
        }
    }

    @GetMapping("/byCodePaymentDate")
    public ResponseEntity<GenericResponse<List<Payment>>> getByCodePaymentDate(@RequestParam String code, @RequestParam String paymentDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return ResponseEntity.ok(paymentService.findByCodeAndPaymentDate(code, dateTime));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "Incorrect date format. Use yyyy-MM-dd HH:mm:ss", null));
        }
    }

    @GetMapping("/listAll")
    public ResponseEntity<GenericResponse<List<Payment>>> listAll() {
        return ResponseEntity.ok(paymentService.findAllPayments());
    }


    @GetMapping("/distinct/codes")
    public ResponseEntity<GenericResponse<List<String>>> getDistinctCodes() {
        return ResponseEntity.ok(paymentService.findAllDistinctCodes());
    }

    @GetMapping("/distinct/concepts")
    public ResponseEntity<GenericResponse<List<String>>> getDistinctConcepts() {
        return ResponseEntity.ok(paymentService.findAllDistinctConcepts());
    }

    @GetMapping("/distinct/paymentDates")
    public ResponseEntity<GenericResponse<List<LocalDateTime>>> getDistinctPaymentDates() {
        return ResponseEntity.ok(paymentService.findAllDistinctPaymentDates());
    }

    @GetMapping("/distinct/names")
    public ResponseEntity<GenericResponse<List<String>>> getDistinctNames() {
        return ResponseEntity.ok(paymentService.findAllDistinctNames());
    }
}