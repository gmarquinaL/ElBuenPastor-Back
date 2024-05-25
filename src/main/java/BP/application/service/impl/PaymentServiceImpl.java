package BP.application.service.impl;

import BP.application.dto.PaymentDTO;
import BP.domain.dao.IPaymentRepo;
import BP.domain.entity.Payment;
import BP.application.util.GenericResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl {
    @Autowired
    private IPaymentRepo paymentRepository;

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

    @Transactional
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("data", -1, "The uploaded file is empty.", null));
        }

        List<Payment> payments = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int headerRow = 5;
            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null || isTotalRow(currentRow)) {
                    continue;
                }
                Payment payment = mapRowToPayment(currentRow);
                if (payment != null) {
                    payments.add(payment);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing the file: " + e.getMessage(), e);
        }

        paymentRepository.saveAll(payments);
        List<PaymentDTO> paymentDTOs = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "File processed successfully", paymentDTOs));
    }

    private boolean isTotalRow(Row row) {
        if (row == null) return false;
        Cell cell = row.getCell(5);
        return cell != null && cell.getCellType() == CellType.STRING && "Total".equals(cell.getStringCellValue().trim());
    }

    private Payment mapRowToPayment(Row row) {
        if (row == null) return null;
        Payment payment = new Payment();
        payment.setCode(getCellValue(row, 2));
        payment.setName(getCellValue(row, 3));
        payment.setReferenceDoc(getCellValue(row, 4));
        payment.setConcept(getCellValue(row, 5));
        payment.setAmount(new BigDecimal(getCellValue(row, 6)));
        payment.setPaymentDate(convertExcelDateToLocalDateTime(row.getCell(7)));
        payment.setAgency(getCellValue(row, 8));
        payment.setDueDate(convertExcelDateToLocalDate(row.getCell(9)));
        payment.setPaymentMethod(getCellValue(row, 10));
        payment.setUsername(getCellValue(row, 11));
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
            return cell.getStringCellValue();
        }
        return "";
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByName(String name) {
        List<Payment> payments = paymentRepository.findByName(name);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by name", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByCode(String code) {
        List<Payment> payments = paymentRepository.findByCode(code);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by code", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByConcept(String concept) {
        List<Payment> payments = paymentRepository.findByConcept(concept);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by concept", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findByPaymentDate(LocalDateTime dateTime) {
        List<Payment> payments = paymentRepository.findByPaymentDate(dateTime);
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payments found by payment date", dtos));
    }

    public ResponseEntity<GenericResponse<List<PaymentDTO>>> findAllPaymentsDTO() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentDTO> dtos = payments.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All payments retrieved", dtos));
    }

    public ResponseEntity<GenericResponse<List<String>>> findAllDistinctCodesDTO() {
        List<String> codes = paymentRepository.findDistinctCode();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct codes retrieved", codes));
    }

    public ResponseEntity<GenericResponse<List<String>>> findAllDistinctConceptsDTO() {
        List<String> concepts = paymentRepository.findDistinctConcept();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct concepts retrieved", concepts));
    }

    public ResponseEntity<GenericResponse<List<LocalDateTime>>> findAllDistinctPaymentDatesDTO() {
        List<LocalDateTime> paymentDates = paymentRepository.findDistinctPaymentDate();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct payment dates retrieved", paymentDates));
    }

    public ResponseEntity<GenericResponse<List<String>>> findAllDistinctNamesDTO() {
        List<String> names = paymentRepository.findDistinctName();
        return ResponseEntity.ok(new GenericResponse<>("data", 1, "All distinct names retrieved", names));
    }
}
