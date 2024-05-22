package BP.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 50)
    @NotBlank(message = "El código no puede estar vacío")
    private String code;

    @Column(name = "name", length = 100)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Column(name = "reference_doc", length = 50)
    @NotBlank(message = "El documento de referencia no puede estar vacío")
    private String referenceDoc;

    @Column(name = "concept", length = 100)
    @NotBlank(message = "El concepto no puede estar vacío")
    private String concept;

    @Column(name = "amount", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor que cero")
    private BigDecimal amount;

    @Column(name = "agency", length = 50)
    @NotBlank(message = "La agencia no puede estar vacía")
    private String agency;

    @Column(name = "payment_date")
    @NotNull(message = "La fecha de pago no puede ser nula")
    private LocalDateTime paymentDate;

    @Column(name = "due_date")
    @NotNull(message = "La fecha de vencimiento no puede ser nula")
    private LocalDate dueDate;

    @Column(name = "payment_method", length = 50)
    @NotBlank(message = "El medio de pago no puede estar vacío")
    private String paymentMethod;

    @Column(name = "username", length = 50)
    @NotBlank(message = "El usuario no puede estar vacío")
    private String username;
}
