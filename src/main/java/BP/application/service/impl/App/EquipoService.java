package BP.application.service.impl.App;

import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.App.Equipment;
import BP.domain.entity.App.Location;
import BP.domain.entity.App.Teacher;
import BP.domain.dao.App.EquipoRepository;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@Transactional
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    public BestGenericResponse<Equipment> addEquipo(Equipment equipo) {
        try {
            // Verificar si el código patrimonial ya existe
            Optional<Equipment> existingPatrimonialCode = equipoRepository.findByAssetCode(equipo.getAssetCode());
            if (existingPatrimonialCode.isPresent()) {
                return new BestGenericResponse<>(Global.TIPO_CUIDADO, Global.RPTA_WARNING, "El código patrimonial ya existe.", null);
            }

            // Generar y verificar un código de barras único
            String barcode;
            do {
                barcode = generateRandomBarcode(12);
            } while (equipoRepository.findByBarcode(barcode).isPresent());

            equipo.setBarcode(barcode);
            Equipment savedEquipo = equipoRepository.save(equipo);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Equipo registrado exitosamente.", savedEquipo);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al registrar el equipo: " + e.getMessage(), null);
        }
    }

    private String generateRandomBarcode(int length) {
        String characters = "0123456789";
        Random rng = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(rng.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public BestGenericResponse<Equipment> updateEquipo(Equipment equipo) {
        if (equipo.getId() == 0) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "ID de equipo no proporcionado.", null);
        }

        Optional<Equipment> existingEquipo = equipoRepository.findById(equipo.getId());
        if (!existingEquipo.isPresent()) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Equipo no encontrado.", null);
        }

        try {
            Equipment updatedEquipo = existingEquipo.get();

            // Updating only allowed fields
            updatedEquipo.setEquipmentType(equipo.getEquipmentType());
            updatedEquipo.setDescription(equipo.getDescription());
            updatedEquipo.setStatus(equipo.getStatus());
            updatedEquipo.setBrand(equipo.getBrand());
            updatedEquipo.setModel(equipo.getModel());
            updatedEquipo.setEquipmentName(equipo.getEquipmentName());
            updatedEquipo.setOrderNumber(equipo.getOrderNumber());
            updatedEquipo.setSerial(equipo.getSerial());

            // Assigning responsible and location if provided
            if (equipo.getResponsible() != null && equipo.getResponsible().getId() > 0) {
                updatedEquipo.setResponsible(new Teacher(equipo.getResponsible().getId()));
            }
            if (equipo.getLocation() != null && equipo.getLocation().getId() > 0) {
                updatedEquipo.setLocation(new Location(equipo.getLocation().getId()));
            }


            equipoRepository.save(updatedEquipo);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Equipo actualizado exitosamente.", updatedEquipo);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al actualizar el equipo: " + e.getMessage(), null);
        }
    }

    public BestGenericResponse<Void> deleteEquipo(Integer id) {
        if (!equipoRepository.existsById(id)) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Equipo no encontrado.", null);
        }
        try {
            equipoRepository.deleteById(id);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Equipo eliminado exitosamente.", null);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al eliminar el equipo: " + e.getMessage(), null);
        }
    }

    public BestGenericResponse<List<Equipment>> findAllEquipos() {
        try {
            List<Equipment> equipos = (List<Equipment>) equipoRepository.findAll();
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Listado completo de equipos.", equipos);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener el listado de equipos: " + e.getMessage(), null);
        }
    }

    public BestGenericResponse<Equipment> scanAndCopyBarcodeData(MultipartFile file) {
        if (file.isEmpty()) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_WARNING, "El archivo está vacío.", null);
        }
        try (InputStream inputStream = file.getInputStream()) {
            // Redimensionar la imagen para reducir el uso de memoria
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                System.out.println("La imagen cargada es nula o el formato no es compatible.");
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_WARNING, "La imagen cargada es nula o el formato no es compatible.", null);
            }

            int targetWidth = 800; // Ancho objetivo
            int targetHeight = (originalImage.getHeight() * targetWidth) / originalImage.getWidth();
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();

            LuminanceSource source = new BufferedImageLuminanceSource(resizedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            String barcodeText = result.getText().trim();

            System.out.println("Código de barras decodificado (limpio): '" + barcodeText + "'");

            Optional<Equipment> informacion = equipoRepository.findByBarcode(barcodeText);
            if (informacion.isPresent()) {
                System.out.println("Código de barras encontrado en la base de datos: " + barcodeText);
                return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Escaneo de Código de Barras correcto", informacion.get());
            } else {
                System.out.println("Código de barras no encontrado en la base de datos: " + barcodeText);
                return new BestGenericResponse<>(Global.TIPO_CUIDADO, Global.RPTA_WARNING, "Código de barras no encontrado", null);
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el archivo: " + e.getMessage());
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al procesar el archivo: " + e.getMessage(), null);
        }
    }



    public BestGenericResponse<byte[]> generateBarcodeImageForPatrimonialCode(String patrimonialCode) {
        Optional<Equipment> informacion = equipoRepository.findByAssetCode(patrimonialCode);
        if (informacion.isPresent()) {
            try {
                Equipment info = informacion.get();
                String barcodeData = info.getBarcode();  // Usa el código de barras almacenado
                return new BestGenericResponse<>("SUCCESS", Global.RPTA_OK, "Código de barras generado exitosamente", generateBarcodeImage(barcodeData));
            } catch (Exception e) {
                return new BestGenericResponse<>("ERROR", Global.RPTA_ERROR, "Error al generar el código de barras: " + e.getMessage(), null);
            }
        } else {
            return new BestGenericResponse<>("ERROR", Global.RPTA_ERROR, "No se encontró información para el código patrimonial proporcionado", null);
        }
    }
    private byte[] generateBarcodeImage(String data) throws Exception {
        MultiFormatWriter barcodeWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_128, 300, 100);

        // Convert BitMatrix to BufferedImage
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Prepare to add text below barcode and margin at the top
        BufferedImage combinedImage = new BufferedImage(barcodeImage.getWidth(), barcodeImage.getHeight() + 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        // Fill background with white
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, combinedImage.getWidth(), combinedImage.getHeight());

        // Draw barcode image with top margin
        g.drawImage(barcodeImage, 0, 20, null);
        g.setColor(java.awt.Color.BLACK);
        g.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12));

        // Draw text below barcode
        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(data);
        int textX = (barcodeImage.getWidth() - textWidth) / 2; // Center text horizontally
        int textY = barcodeImage.getHeight() + 40; // Position text below the barcode
        g.drawString(data, textX, textY);

        g.dispose(); // Clean up graphics object

        // Write combined image to output byte array
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(combinedImage, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    // Método para generar el Excel
    public byte[] generateExcelReport() throws Exception {
        List<Equipment> equipos = (List<Equipment>) equipoRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Equipment");

            // Insertar el logo
            try (FileInputStream inputStream = new FileInputStream("src/main/resources/logo.png")) {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                CreationHelper helper = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0); // Columna A
                anchor.setRow1(0); // Fila 1
                anchor.setCol2(3); // Extender a la columna D
                anchor.setRow2(10); // Extender a la fila 10
                Picture pict = drawing.createPicture(anchor, pictureIdx);
                // Ajustar la imagen a 135x135 píxeles
                pict.resize(1);
                pict.resize(75 / pict.getImageDimension().getWidth(), 58 / pict.getImageDimension().getHeight());
            }

            // Crear un estilo para el título
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            // Agregar título en la fila 3
            Row titleRow = sheet.createRow(2); // Fila 3
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE TOTAL DE EQUIPOS");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 15)); // Fusionar celdas para el título

            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Crear el encabezado en la fila 6
            String[] columns = {
                    "ID", "Tipo Equipo", "Código Barra", "Código Patrimonial", "Descripción", "Estado",
                    "Fecha Compra", "Marca", "Modelo", "Nombre Equipo", "Número Orden", "Serie",
                    "Responsable - Nombre", "Responsable - Cargo", "Ubicación - Ambiente", "Ubicación - Ubicación Física"
            };
            Row headerRow = sheet.createRow(5); // Fila 6
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos a partir de la fila 7
            int rowIdx = 6;
            for (Equipment equipment : equipos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(equipment.getId());
                row.createCell(1).setCellValue(equipment.getEquipmentType());
                row.createCell(2).setCellValue(equipment.getBarcode());
                row.createCell(3).setCellValue(equipment.getAssetCode());
                row.createCell(4).setCellValue(equipment.getDescription());
                row.createCell(5).setCellValue(equipment.getStatus());
                row.createCell(6).setCellValue(equipment.getBrand());
                row.createCell(7).setCellValue(equipment.getModel());
                row.createCell(8).setCellValue(equipment.getEquipmentName());
                row.createCell(9).setCellValue(equipment.getOrderNumber());
                row.createCell(10).setCellValue(equipment.getSerial());
                row.createCell(11).setCellValue(equipment.getResponsible() != null ? equipment.getResponsible().getFullName() : "-");
                row.createCell(12).setCellValue(equipment.getResponsible() != null ? equipment.getResponsible().getPosition() : "-");
                row.createCell(13).setCellValue(equipment.getLocation() != null ? equipment.getLocation().getRoom() : "-");
                row.createCell(14).setCellValue(equipment.getLocation() != null ? equipment.getLocation().getPhysicalLocation() : "-");
            }

            // Ajuste automático de tamaño de columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new Exception("Error al generar el reporte: " + e.getMessage());
        }
    }



    public BestGenericResponse<Equipment> getEquipoById(int id) {
        try {
            Equipment equipo = equipoRepository.findByIdWithDetails(id);
            if (equipo != null) {
                return new BestGenericResponse<>(Global.TIPO_DATA, Global.RPTA_OK, Global.OPERACION_CORRECTA, equipo);
            } else {
                return new BestGenericResponse<>(Global.TIPO_RESULT, Global.RPTA_WARNING, "El equipo no existe", null);
            }
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, Global.OPERACION_ERRONEA, null);
        }
    }

    public BestGenericResponse<List<Equipment>> filtroPorNombre(String nombreEquipo) {
        try {
            List<Equipment> equipos = equipoRepository.findByEquipmentNameContaining(nombreEquipo);
            return new BestGenericResponse<>(Global.TIPO_DATA, Global.RPTA_OK, Global.OPERACION_CORRECTA, equipos);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, Global.OPERACION_ERRONEA, null);
        }
    }

    public BestGenericResponse<List<Equipment>> filtroCodigoPatrimonial(String assetCode) {
        try {
            List<Equipment> equipos = equipoRepository.findByAssetCodeContaining(assetCode);
            return new BestGenericResponse<>(Global.TIPO_DATA, Global.RPTA_OK, Global.OPERACION_CORRECTA, equipos);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, Global.OPERACION_ERRONEA, null);
        }
    }

    public BestGenericResponse<List<Equipment>> filtroFechaCompraBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<Equipment> equipos = equipoRepository.findByPurchaseDateBetween(fechaInicio, fechaFin);
            return new BestGenericResponse<>(Global.TIPO_DATA, Global.RPTA_OK, Global.OPERACION_CORRECTA, equipos);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, Global.OPERACION_ERRONEA, null);
        }
    }
}
