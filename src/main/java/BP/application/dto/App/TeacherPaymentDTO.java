package BP.application.dto.App;

import BP.domain.entity.App.Foto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TeacherPaymentDTO {
    private int id;
    private int teacherId;
    private String teacherName;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate paymentDate;
    private String paymentStatus;
    private String paymentReference;
    private int workDays;
    private String educationLevel;
    private String modularCode;
    private Foto foto;
    public TeacherPaymentDTO() {
    }

    public TeacherPaymentDTO(int id) {
        this.id = id;
    }

    public TeacherPaymentDTO(int id, int teacherId, String teacherName, BigDecimal amount, LocalDate paymentDate, String paymentStatus, String paymentReference, int workDays, String educationLevel, String modularCode) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.paymentReference = paymentReference;
        this.workDays = workDays;
        this.educationLevel = educationLevel;
        this.modularCode = modularCode;
    }
}

