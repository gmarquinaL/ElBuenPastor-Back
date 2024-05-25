package BP.application.service;

import BP.application.dto.PaymentDTO;
import BP.application.util.GenericResponse;
import BP.domain.entity.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

public interface IPaymentService extends ICRUD<Payment, Long> {

    ResponseEntity<GenericResponse<List<PaymentDTO>>> processPaymentsFile(MultipartFile file);
    boolean isPaymentDuplicate(PaymentDTO paymentDTO);
}
