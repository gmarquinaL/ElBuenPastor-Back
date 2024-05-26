package BP.application.service.impl;

import BP.application.dto.PaymentDTO;
import BP.application.service.IPaymentService;
import BP.application.util.GenericResponse;
import BP.domain.dao.IPaymentRepo;
import BP.domain.entity.Payment;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private IPaymentRepo paymentRepo;

    @Autowired
    private ModelMapper modelMapper;
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .name(payment.getName())
                .concept(payment.getConcept())
                .amount(payment.getAmount())
                .agency(payment.getAgency())
                .paymentDate(payment.getPaymentDate())
                .dueDate(payment.getDueDate())
                .paymentMethod(payment.getPaymentMethod())
                .username(payment.getUsername())
                .build();
    }
    @Override
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> processPaymentsFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "The uploaded file is empty.", null));
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Verificar que el archivo tenga las columnas correctas
            Row headerRow = sheet.getRow(5); // La fila de encabezado es la 6 (índice 5)
            if (!isValidHeaderRow(headerRow)) {
                return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "The uploaded file is not a valid payments file.", null));
            }

            List<Payment> payments = new ArrayList<>();
            int duplicateCount = 0;
            boolean reachedTotal = false;
            for (int i = 6; i <= sheet.getLastRowNum() && !reachedTotal; i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) continue;
                String firstCellContent = getCellValue(currentRow, 2); // Check column C
                if (firstCellContent.equals("Total")) {
                    reachedTotal = true;
                    continue;
                }
                Payment payment = mapRowToPayment(currentRow);
                if (payment != null) {
                    if (isPaymentDuplicate(modelMapper.map(payment, PaymentDTO.class))) {
                        duplicateCount++;
                    } else {
                        payments.add(payment);
                    }
                }
            }

            paymentRepo.saveAll(payments);
            List<PaymentDTO> paymentDTOs = payments.stream()
                    .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                    .collect(Collectors.toList());

            String message;
            if (payments.isEmpty()) {
                message = "All records in the file are duplicates.";
            } else {
                message = payments.size() + " payments were successfully added to the database. " + duplicateCount + " duplicates were skipped.";
            }

            return ResponseEntity.ok(new GenericResponse<>("data", 1, message, paymentDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("data", -1, "Error processing the file: " + e.getMessage(), null));
        }
    }
    private boolean isValidHeaderRow(Row headerRow) {
        if (headerRow == null) return false;

        return "Codigo".equals(getCellValue(headerRow, 2)) &&
                "NOMBRE".equals(getCellValue(headerRow, 3)) &&
                "DOC - REF".equals(getCellValue(headerRow, 4)) &&
                "CONCEPTO".equals(getCellValue(headerRow, 5)) &&
                "MONTO".equals(getCellValue(headerRow, 6)) &&
                "FECHA PAGO".equals(getCellValue(headerRow, 7)) &&
                "AGENCIA".equals(getCellValue(headerRow, 8)) &&
                "FECHA VENCI.".equals(getCellValue(headerRow, 9)) &&
                "MEDIO PAGO".equals(getCellValue(headerRow, 10)) &&
                "USUARIO".equals(getCellValue(headerRow, 11));
    }

    @Override
    public boolean isPaymentDuplicate(PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        return paymentRepo.findByAgencyAndCodeAndConceptAndReferenceDocAndPaymentDateAndDueDateAndPaymentMethodAndAmountAndNameAndUsername(
                payment.getAgency(), payment.getCode(), payment.getConcept(), payment.getReferenceDoc(), payment.getPaymentDate(),
                payment.getDueDate(), payment.getPaymentMethod(), payment.getAmount(), payment.getName(), payment.getUsername()
        ).isPresent();
    }

    private Payment mapRowToPayment(Row row) {
        if (row == null) return null;
        Payment payment = new Payment();
        payment.setCode(getCellValue(row, 2)); // Column C
        payment.setName(getCellValue(row, 3)); // Column D
        payment.setReferenceDoc(getCellValue(row, 4)); // Column E
        payment.setConcept(getCellValue(row, 5)); // Column F
        payment.setAmount(new BigDecimal(getCellValue(row, 6))); // Column G
        payment.setPaymentDate(convertExcelDateToLocalDateTime(row.getCell(7))); // Column H
        payment.setAgency(getCellValue(row, 8)); // Column I
        payment.setDueDate(convertExcelDateToLocalDate(row.getCell(9))); // Column J
        payment.setPaymentMethod(getCellValue(row, 10)); // Column K
        payment.setUsername(getCellValue(row, 11)); // Column L

        // Validate required fields
        if (payment.getCode() == null || payment.getCode().isEmpty() ||
                payment.getName() == null || payment.getName().isEmpty() ||
                payment.getReferenceDoc() == null || payment.getReferenceDoc().isEmpty() ||
                payment.getConcept() == null || payment.getConcept().isEmpty() ||
                payment.getAmount() == null ||
                payment.getPaymentDate() == null ||
                payment.getAgency() == null || payment.getAgency().isEmpty() ||
                payment.getDueDate() == null ||
                payment.getPaymentMethod() == null || payment.getPaymentMethod().isEmpty() ||
                payment.getUsername() == null || payment.getUsername().isEmpty()) {
            return null; // Skip invalid rows
        }

        return payment;
    }

    private LocalDateTime convertExcelDateToLocalDateTime(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        Date date = cell.getDateCellValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private LocalDate convertExcelDateToLocalDate(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        Date date = cell.getDateCellValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue().trim();
        }
        return "";
    }

    @Override
    public Payment save(Payment entity) {
        return paymentRepo.save(entity);
    }

    @Override
    public Payment update(Payment entity, Long id) {
        return paymentRepo.findById(id).map(existingPayment -> {
            existingPayment.setAgency(entity.getAgency());
            existingPayment.setCode(entity.getCode());
            existingPayment.setConcept(entity.getConcept());
            existingPayment.setReferenceDoc(entity.getReferenceDoc());
            existingPayment.setPaymentDate(entity.getPaymentDate());
            existingPayment.setDueDate(entity.getDueDate());
            existingPayment.setPaymentMethod(entity.getPaymentMethod());
            existingPayment.setAmount(entity.getAmount());
            existingPayment.setName(entity.getName());
            existingPayment.setUsername(entity.getUsername());
            return paymentRepo.save(existingPayment);
        }).orElseGet(() -> paymentRepo.save(entity));
    }

    @Override
    public Payment readById(Long id) {
        return paymentRepo.findById(id).orElse(null);
    }

    @Override
    public List<Payment> readAll() {
        return paymentRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        paymentRepo.deleteById(id);
    }
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByName(String name) {
        List<Payment> payments = paymentRepo.findByName(name);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by name", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByCode(String code) {
        List<Payment> payments = paymentRepo.findByCode(code);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by code", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByConcept(String concept) {
        List<Payment> payments = paymentRepo.findByConcept(concept);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by concept", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByPaymentDate(LocalDateTime dateTime) {
        List<Payment> payments = paymentRepo.findByPaymentDate(dateTime);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by payment date", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findAllPaymentsDTO() {
        List<Payment> payments = paymentRepo.findAll();
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All payments retrieved", dtos));
    }

    public ResponseEntity<GenericResponse<List<String>>> findAllDistinctCodesDTO() {
        List<String> codes = paymentRepo.findDistinctCode();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct codes retrieved", codes));
    }

    public ResponseEntity<GenericResponse<List<String>>> findAllDistinctConceptsDTO() {
        List<String> concepts = paymentRepo.findDistinctConcept();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct concepts retrieved", concepts));
    }

    public ResponseEntity<GenericResponse<List<LocalDateTime>>> findAllDistinctPaymentDatesDTO() {
        List<LocalDateTime> paymentDates = paymentRepo.findDistinctPaymentDate();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct payment dates retrieved", paymentDates));
    }

    public ResponseEntity<GenericResponse<List<String>>> findAllDistinctNamesDTO() {
        List<String> names = paymentRepo.findDistinctName();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct names retrieved", names));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<GenericResponse<PaymentDTO>> addPayment(PaymentDTO paymentDTO) {
        try {
            Payment payment = modelMapper.map(paymentDTO, Payment.class);
            payment = paymentRepo.save(payment);
            return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payment added successfully", modelMapper.map(payment, PaymentDTO.class)));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("data", -1, "Failed to add payment due to data integrity issues: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("data", -1, "Error adding payment: " + e.getMessage(), null));
        }
    }


    @Transactional
    public ResponseEntity<GenericResponse<PaymentDTO>> editPayment(Long id, PaymentDTO paymentDTO) {
        try {
            Payment existingPayment = paymentRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Update only the specific fields
            existingPayment.setName(paymentDTO.getName());
            existingPayment.setConcept(paymentDTO.getConcept());
            existingPayment.setAmount(paymentDTO.getAmount());
            existingPayment.setPaymentDate(paymentDTO.getPaymentDate());
            existingPayment.setDueDate(paymentDTO.getDueDate());

// Save the updated payment
                    existingPayment = paymentRepo.save(existingPayment);

            // Return success response
            return ResponseEntity.ok(new GenericResponse<>(
                    "data",
                    1,
                    "Payment updated successfully",
                    modelMapper.map(existingPayment, PaymentDTO.class)
            ));
        } catch (Exception e) {
            // Log and return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(
                            "data",
                            -1,
                            "Error updating payment: " + e.getMessage(),
                            null
                    ));
        }
    }


    @Transactional
    public ResponseEntity<GenericResponse<Void>> deletePayment(Long id) {
        try {
            if (!paymentRepo.existsById(id)) {
                return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "Payment not found", null));
            }
            paymentRepo.deleteById(id);
            return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payment deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("data", -1, "Error deleting payment: " + e.getMessage(), null));
        }
    }

}
