package BP.application.service.impl;

import BP.application.util.GenericResponse;
import BP.domain.dao.IPaymentRepo;
import BP.domain.entity.Payment;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class PaymentServiceImpl {
    @Autowired
    private IPaymentRepo paymentRepository;

    // 1. Filter by Name
    public GenericResponse<List<Payment>> findByName(String name) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByName(name));
    }

    // 2. Filter by Code
    public GenericResponse<List<Payment>> findByCode(String code) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByCode(code));
    }

    // 3. Filter by Concept
    public GenericResponse<List<Payment>> findByConcept(String concept) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByConcept(concept));
    }

    // 4. Filter by Payment Date
    public GenericResponse<List<Payment>> findByPaymentDate(LocalDateTime paymentDate) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByPaymentDate(paymentDate));
    }

    // 5. Filter by Name and Code
    public GenericResponse<List<Payment>> findByNameAndCode(String name, String code) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByNameAndCode(name, code));
    }

    // 6. Filter by Name, Code, and Concept
    public GenericResponse<List<Payment>> findByNameCodeAndConcept(String name, String code, String concept) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByNameAndCodeAndConcept(name, code, concept));
    }

    // 7. Filter by Name, Code, Concept, and Payment Date
    public GenericResponse<List<Payment>> findByNameCodeConceptAndPaymentDate(String name, String code, String concept, LocalDateTime paymentDate) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByNameAndCodeAndConceptAndPaymentDate(name, code, concept, paymentDate));
    }

    // 8. Filter by Code and Concept
    public GenericResponse<List<Payment>> findByCodeAndConcept(String code, String concept) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByCodeAndConcept(code, concept));
    }

    // 9. Filter by Concept and Payment Date
    public GenericResponse<List<Payment>> findByConceptAndPaymentDate(String concept, LocalDateTime paymentDate) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByConceptAndPaymentDate(concept, paymentDate));
    }

    // 10. Filter by Code and Payment Date
    public GenericResponse<List<Payment>> findByCodeAndPaymentDate(String code, LocalDateTime paymentDate) {
        return new GenericResponse<>("data", 1, "Payments found", paymentRepository.findByCodeAndPaymentDate(code, paymentDate));
    }

    // 11. Method to list all payments
    public GenericResponse<List<Payment>> findAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return new GenericResponse<>("data", 1, "All payments retrieved", payments);
    }

    public GenericResponse<List<String>> findAllDistinctCodes() {
        return new GenericResponse<>("data", 1, "All distinct codes retrieved", paymentRepository.findDistinctCode());
    }

    public GenericResponse<List<String>> findAllDistinctConcepts() {
        return new GenericResponse<>("data", 1, "All distinct concepts retrieved", paymentRepository.findDistinctConcept());
    }

    public GenericResponse<List<LocalDateTime>> findAllDistinctPaymentDates() {
        return new GenericResponse<>("data", 1, "All distinct payment dates retrieved", paymentRepository.findDistinctPaymentDate());
    }

    public GenericResponse<List<String>> findAllDistinctNames() {
        return new GenericResponse<>("data", 1, "All distinct names retrieved", paymentRepository.findDistinctName());
    }

    @Transactional
    public List<Payment> storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("The uploaded file is empty.");
        }

        List<Payment> payments = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int headerRow = 5;

            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null || isTotalRow(currentRow)) {
                    continue;  // Better to continue and process what we can
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
        return payments;
    }

    private boolean isTotalRow(Row row) {
        if (row == null) return false;
        Cell cell = row.getCell(5); // Check cell F
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
}
