package BP.application.service.impl.App;

import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.App.Teacher;
import BP.domain.entity.App.TeacherPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private PagosService pagosService;  // Cambio para usar PagosService

    // Enviar notificación de pago a un docente específico
    public BestGenericResponse<String> enviarNotificacionPago(int paymentId) {
        BestGenericResponse<TeacherPayment> paymentResponse = pagosService.obtenerPagoPorId(paymentId);
        if (paymentResponse.getRpta() == Global.RPTA_OK && paymentResponse.getBody().getPaymentStatus().equals("Pendiente")) {
            TeacherPayment payment = paymentResponse.getBody();
            Teacher teacher = payment.getTeacher();
            String message = "Estimado " + teacher.getFullName() + ", tiene un nuevo pago programado de " + payment.getAmount() + " para la fecha " + payment.getPaymentDate() + ".";
            System.out.println("Notificación enviada a: " + teacher.getEmail() + " Mensaje: " + message);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Notificación enviada correctamente", message);
        } else {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no está pendiente o no encontrado", null);
        }
    }
}
