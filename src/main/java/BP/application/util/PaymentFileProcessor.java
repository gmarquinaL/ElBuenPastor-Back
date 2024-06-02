package BP.application.util;

import BP.application.dto.PaymentDTO;
import BP.domain.dao.IPaymentRepo;
import BP.domain.entity.Payment;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class PaymentFileProcessor {

    @Autowired
    private IPaymentRepo paymentRepo;

    @Autowired
    private ModelMapper modelMapper;
    private static final Logger log = LoggerFactory.getLogger(PaymentFileProcessor.class);

    public GenericResponse<List<PaymentDTO>> processFile(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(5);
            if (!isValidHeaderRow(headerRow)) {
                return new GenericResponse<>("data", -3, "El archivo subido no es un archivo de pagos válido.", null);
            }

            List<Payment> payments = new ArrayList<>();
            int newPaymentsCount = 0;
            int duplicateCount = 0;
            for (int i = 6; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null || "Total".equals(getCellValue(currentRow, 5))) {
                    break; // Detiene el procesamiento cuando encuentra "Total" en la columna F.
                }
                Payment payment = mapRowToPayment(currentRow);
                if (payment != null && !isPaymentDuplicate(payment)) {
                    payments.add(payment);
                    newPaymentsCount++;
                } else {
                    duplicateCount++;
                }
            }

            paymentRepo.saveAll(payments);
            List<PaymentDTO> paymentDTOs = payments.stream()
                    .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                    .collect(Collectors.toList());

            String message = newPaymentsCount == 0 ? "Todos los registros son duplicados." :
                    String.format("%d pagos nuevos agregados, %d duplicados omitidos.", newPaymentsCount, duplicateCount);

            return new GenericResponse<>("data", 1, message, paymentDTOs);
        } catch (Exception e) {
            log.error("Error processing file", e);
            return new GenericResponse<>("data", -1, "Error al procesar el archivo: " + e.getMessage(), null);
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

    private Payment mapRowToPayment(Row row) {
        if (row == null) return null;
        // Comprueba si la celda en la columna F contiene la palabra "Total"
        if ("Total".equals(getCellValue(row, 5))) {
            return null; // Retorna null para indicar que no se debe procesar esta fila.
        }

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

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue().trim();
        }
        return "";
    }

    private LocalDateTime convertExcelDateToLocalDateTime(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            return null; // Asegura que la celda no es nula, es numérica y está formateada como fecha.
        }
        Date date = cell.getDateCellValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private LocalDate convertExcelDateToLocalDate(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            return null;
        }
        Date date = cell.getDateCellValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public boolean isPaymentDuplicate(Payment payment) {
        List<Payment> existingPayments = paymentRepo.findAllByAllFields(
                payment.getAgency(),
                payment.getAmount(),
                payment.getCode(),
                payment.getConcept(),
                payment.getDueDate(),
                payment.getName(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getReferenceDoc(),
                payment.getUsername()
        );
        if (!existingPayments.isEmpty()) {
            log.debug("Found duplicate with exact timestamp: " + existingPayments.get(0).getPaymentDate());
        }
        return !existingPayments.isEmpty();
    }

}
