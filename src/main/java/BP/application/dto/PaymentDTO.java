package BP.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private String code;
    private String name;
    private String referenceDoc;
    private String concept;
    private BigDecimal amount;
    private String agency;
    private LocalDateTime paymentDate;
    private LocalDate dueDate;
    private String paymentMethod;
    private String username;
}
