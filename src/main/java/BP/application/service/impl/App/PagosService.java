package BP.application.service.impl.App;


import BP.application.dto.App.TeacherPaymentDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.App.PagoRepository;
import BP.domain.entity.App.Teacher;
import BP.domain.entity.App.TeacherPayment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagosService {

    @Autowired
    private PagoRepository pagoRepository;

    public BestGenericResponse<TeacherPayment> agregarPago(TeacherPayment pago) {
        try {
            pago = cleanTeacherPayment(pago);
            pago.setPaymentStatus("Pendiente");  // Asegurar que el pago se agregue como pendiente
            TeacherPayment savedPago = pagoRepository.save(pago);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pago agregado correctamente", savedPago);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al agregar el pago", null);
        }
    }

    public BestGenericResponse<String> aceptarNotificacionPago(int teacherId, int paymentId) {
        Optional<TeacherPayment> paymentOpt = pagoRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            TeacherPayment payment = paymentOpt.get();
            if (payment.getPaymentStatus().equals("Pagado")) {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "La notificación ya ha sido aceptada", null);
            }
            if (payment.getTeacher().getId() == teacherId && payment.getPaymentStatus().equals("Pendiente")) {
                payment.setPaymentStatus("Pagado");  // Cambiar estado a pagado
                pagoRepository.save(payment);
                return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pago aceptado y marcado como pagado", "Pago aceptado");
            } else {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no está pendiente o no corresponde al docente", null);
            }
        } else {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no encontrado", null);
        }
    }

    public BestGenericResponse<TeacherPayment> editarPago(TeacherPayment pago) {
        if (!pagoRepository.existsById(pago.getId())) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no encontrado", null);
        }
        try {
            TeacherPayment existingPago = pagoRepository.findById(pago.getId()).orElseThrow(() -> new Exception("Pago no encontrado"));

            pago = cleanTeacherPayment(pago);

            existingPago.setAmount(pago.getAmount());
            existingPago.setPaymentDate(pago.getPaymentDate());
            existingPago.setPaymentStatus(pago.getPaymentStatus());
            existingPago.setPaymentReference(pago.getPaymentReference());
            existingPago.setWorkDays(pago.getWorkDays());
            existingPago.setEducationLevel(pago.getEducationLevel());
            existingPago.setModularCode(pago.getModularCode());
            existingPago.setTeacher(new Teacher(pago.getTeacher().getId())); // Set only the ID

            TeacherPayment updatedPago = pagoRepository.save(existingPago);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pago actualizado con éxito", updatedPago);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al actualizar el pago", null);
        }
    }


    public BestGenericResponse<Void> eliminarPago(Integer id) {
        if (!pagoRepository.existsById(id)) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no encontrado", null);
        }
        try {
            pagoRepository.deleteById(id);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pago eliminado correctamente", null);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al eliminar el pago", null);
        }
    }

    @Transactional(readOnly = true)
    public BestGenericResponse<List<TeacherPaymentDTO>> listarTodosLosPagos() {
        try {
            List<TeacherPayment> pagos = pagoRepository.findAll();
            List<TeacherPaymentDTO> pagosDTO = pagos.stream().map(pago -> {
                TeacherPaymentDTO dto = new TeacherPaymentDTO();
                dto.setId(pago.getId());
                dto.setTeacherId(pago.getTeacher().getId());
                dto.setTeacherName(pago.getTeacher().getFullName());
                dto.setAmount(pago.getAmount());
                dto.setPaymentDate(pago.getPaymentDate());
                dto.setPaymentStatus(pago.getPaymentStatus());
                dto.setPaymentReference(pago.getPaymentReference());
                dto.setWorkDays(pago.getWorkDays());
                dto.setEducationLevel(pago.getEducationLevel());
                dto.setModularCode(pago.getModularCode());
                dto.setFoto(pago.getFoto());
                return dto;
            }).collect(Collectors.toList());
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Lista de todos los pagos", pagosDTO);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener la lista de pagos", null);
        }
    }

    // Punto 7: Generar baucher de pago en PDF para un docente específico
    public byte[] generarBaucherPdf(TeacherPayment pago) throws Exception {
        // Lógica para generar el archivo PDF del baucher de pago
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(new Paragraph("Baucher de Pago"));
        document.add(new Paragraph("Docente: " + pago.getTeacher().getFullName()));
        document.add(new Paragraph("Monto: " + pago.getAmount().toString()));
        document.add(new Paragraph("Fecha de Pago: " + pago.getPaymentDate().toString()));
        document.close();
        return outputStream.toByteArray();
    }

    // Punto 8: Generar reporte de todos los pagos en formato Excel
    public byte[] generateExcelReport() throws Exception {
        List<TeacherPayment> pagos = pagoRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Pagos");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Docente", "Monto", "Fecha de Pago", "Estado"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle(workbook));
            }

            int rowNum = 1;
            for (TeacherPayment pago : pagos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(pago.getId());
                row.createCell(1).setCellValue(pago.getTeacher().getFullName());
                row.createCell(2).setCellValue(pago.getAmount().doubleValue());
                row.createCell(3).setCellValue(pago.getPaymentDate().toString());
                row.createCell(4).setCellValue(pago.getPaymentStatus());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle headerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public BestGenericResponse<TeacherPayment> obtenerPagoPorId(Integer id) {
        try {
            TeacherPayment pago = pagoRepository.findById(id).orElse(null);
            if (pago == null) {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no encontrado", null);
            }
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pago encontrado", pago);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener el pago", null);
        }
    }
    public BestGenericResponse<List<TeacherPaymentDTO>> listarPagosPorDocenteId(int teacherId) {
        try {
            List<TeacherPayment> pagos = pagoRepository.findByTeacherId(teacherId);
            List<TeacherPaymentDTO> pagosDTO = pagos.stream()
                    .map(this::convertToDTO) // Suponiendo que tienes un método convertToDTO
                    .collect(Collectors.toList());
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pagos del docente obtenidos correctamente", pagosDTO);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener los pagos del docente", null);
        }
    }


    private TeacherPaymentDTO convertToDTO(TeacherPayment pago) {
        TeacherPaymentDTO dto = new TeacherPaymentDTO();
        dto.setId(pago.getId());
        dto.setAmount(pago.getAmount());
        dto.setPaymentDate(pago.getPaymentDate());
        dto.setPaymentStatus(pago.getPaymentStatus());
        dto.setWorkDays(pago.getWorkDays());
        dto.setEducationLevel(pago.getEducationLevel());
        dto.setModularCode(pago.getModularCode());
        dto.setPaymentReference(pago.getPaymentReference());
        dto.setTeacherId(pago.getTeacher().getId());
        dto.setTeacherName(pago.getTeacher().getFullName());
        return dto;
    }
    @Transactional(readOnly = true)
    public BestGenericResponse<TeacherPaymentDTO> obtenerPagoPorSuId(Integer id) {
        try {
            TeacherPayment pago = pagoRepository.findById(id).orElse(null);
            if (pago == null) {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no encontrado", null);
            }
            TeacherPaymentDTO dto = convertToDTO(pago);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Pago encontrado", dto);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener el pago", null);
        }
    }

    private TeacherPayment cleanTeacherPayment(TeacherPayment pago) {
        if (pago.getPaymentStatus() != null) {
            pago.setPaymentStatus(pago.getPaymentStatus().trim());
        }
        if (pago.getPaymentReference() != null) {
            pago.setPaymentReference(pago.getPaymentReference().trim());
        }
        if (pago.getEducationLevel() != null) {
            pago.setEducationLevel(pago.getEducationLevel().trim());
        }
        if (pago.getModularCode() != null) {
            pago.setModularCode(pago.getModularCode().trim());
        }
        if (pago.getTeacher() != null && pago.getTeacher().getFullName() != null) {
            pago.getTeacher().setFullName(pago.getTeacher().getFullName().trim());
        }
        if (pago.getTeacher() != null && pago.getTeacher().getPosition() != null) {
            pago.getTeacher().setPosition(pago.getTeacher().getPosition().trim());
        }
        if (pago.getTeacher() != null && pago.getTeacher().getDni() != null) {
            pago.getTeacher().setDni(pago.getTeacher().getDni().trim());
        }
        if (pago.getTeacher() != null && pago.getTeacher().getEmail() != null) {
            pago.getTeacher().setEmail(pago.getTeacher().getEmail().trim());
        }
        if (pago.getTeacher() != null && pago.getTeacher().getPhone() != null) {
            pago.getTeacher().setPhone(pago.getTeacher().getPhone().trim());
        }
        if (pago.getTeacher() != null && pago.getTeacher().getAddress() != null) {
            pago.getTeacher().setAddress(pago.getTeacher().getAddress().trim());
        }
        return pago;
    }


}
