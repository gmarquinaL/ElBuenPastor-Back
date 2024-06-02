package BP.application.service.impl;

import BP.application.dto.PaymentDTO;
import BP.application.service.IPaymentService;
import BP.application.util.GenericResponse;
import BP.application.util.PaymentFileProcessor;
import BP.domain.dao.IPaymentRepo;
import BP.domain.entity.Payment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private IPaymentRepo paymentRepo;
    @Autowired
    private PaymentFileProcessor paymentFileProcessor;

    @Autowired
    private ModelMapper modelMapper;
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .code(payment.getCode())
                .name(payment.getName())
                .referenceDoc(payment.getReferenceDoc())
                .concept(payment.getConcept())
                .amount(payment.getAmount())
                .agency(payment.getAgency())
                .paymentDate(payment.getPaymentDate())
                .dueDate(payment.getDueDate())
                .paymentMethod(payment.getPaymentMethod())
                .username(payment.getUsername()).build();
    }
    @Override
    public ResponseEntity<GenericResponse<List<PaymentDTO>>> processPaymentsFile(MultipartFile file) {
        GenericResponse<List<PaymentDTO>> response = paymentFileProcessor.processFile(file);
        return ResponseEntity.status(response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @Override
    public boolean isPaymentDuplicate(PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        List<Payment> foundPayments = paymentRepo.findByAgencyAndCodeAndConceptAndReferenceDocAndPaymentDateAndDueDateAndPaymentMethodAndAmountAndNameAndUsername(
                payment.getAgency(), payment.getCode(), payment.getConcept(), payment.getReferenceDoc(), payment.getPaymentDate(),
                payment.getDueDate(), payment.getPaymentMethod(), payment.getAmount(), payment.getName(), payment.getUsername());

        return !foundPayments.isEmpty();
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
            PaymentDTO paymentDTOResponse = modelMapper.map(payment, PaymentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("data", 1, "Payment added successfully", paymentDTOResponse));
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

    // Método para exportar pagos con filtros
    public ResponseEntity<Resource> exportFilteredPayments(String name, LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> filteredPayments = paymentRepo.findByFilters(name, startDate, endDate);
        if (filteredPayments.isEmpty()) {
            return ResponseEntity.badRequest().body(new ByteArrayResource(new byte[]{}));
        }

        Resource excelFile = exportPaymentsToExcel(filteredPayments);
        if (excelFile != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"filtered_payments.xlsx\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public Resource exportPaymentsToExcel(List<Payment> payments) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pagos");

            // Estilos para los bordes y alineación
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER);
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            borderedStyle.setWrapText(true);

            // Estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.cloneStyleFrom(borderedStyle);
            XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 83, 25), new DefaultIndexedColorMap()));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Insertar logo
            InputStream is = getClass().getResourceAsStream("/logo.png");
            byte[] bytes = IOUtils.toByteArray(is);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            is.close();
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(1);
            anchor.setRow1(1);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize();

            // Crear la fila de encabezado en la fila 6
            Row headerRow = sheet.createRow(5);
            String[] headers = {"ID", "Código", "Nombre", "Concepto", "Importe", "Fecha de Pago", "Agencia", "Fecha de Vencimiento", "Método de Pago", "Nombre de Usuario"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Aplicar filtro automático en el encabezado
            sheet.setAutoFilter(new CellRangeAddress(5, 5, 0, headers.length - 1));

            // Llenar datos a partir de la fila 7
            int rowIdx = 6;
            for (Payment payment : payments) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(borderedStyle);
                }
                row.getCell(0).setCellValue(payment.getId());
                row.getCell(1).setCellValue(payment.getCode());
                row.getCell(2).setCellValue(payment.getName());
                row.getCell(3).setCellValue(payment.getConcept());
                row.getCell(4).setCellValue(payment.getAmount().doubleValue());
                row.getCell(5).setCellValue(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(payment.getPaymentDate()));
                row.getCell(6).setCellValue(payment.getAgency());
                row.getCell(7).setCellValue(DateTimeFormatter.ofPattern("dd-MM-yyyy").format(payment.getDueDate()));
                row.getCell(8).setCellValue(payment.getPaymentMethod());
                row.getCell(9).setCellValue(payment.getUsername());
            }

            // Ajustar automáticamente el tamaño de las columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un recurso y devolver
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return new ByteArrayResource(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
