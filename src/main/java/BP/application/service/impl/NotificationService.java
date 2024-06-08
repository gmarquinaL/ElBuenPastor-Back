package BP.application.service.impl;

import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.PagoRepository;
import BP.domain.entity.Teacher;
import BP.domain.entity.TeacherPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private PagoRepository pagoRepository;

    // Enviar notificación de pago a un docente específico
    public BestGenericResponse<String> enviarNotificacionPago(int paymentId) {
        try {
            Optional<TeacherPayment> optionalPayment = pagoRepository.findById(paymentId);
            if (optionalPayment.isEmpty()) {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no encontrado", null);
            }

            TeacherPayment payment = optionalPayment.get();
            Teacher teacher = payment.getTeacher();
            if (teacher == null) {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "El pago no tiene un docente asociado", null);
            }

            // Simula enviar una notificación
            // En un caso real, esto podría ser un correo electrónico, SMS, o notificación push, etc.
            String message = "Estimado " + teacher.getFullName() + ", tiene un nuevo pago programado de " + payment.getAmount() + " para la fecha " + payment.getPaymentDate() + ".";
            System.out.println("Notificación enviada a: " + teacher.getEmail() + " Mensaje: " + message);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Notificación enviada correctamente", message);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al enviar notificación", null);
        }
    }

    // Método para aceptar la notificación de pago (simulado por el docente)
    public BestGenericResponse<String> aceptarNotificacionPago(int teacherId, int paymentId) {
        try {
            // En un entorno real, aquí se actualizarían los registros para reflejar que el docente ha visto y aceptado el pago
            String confirmationMessage = "Docente con ID: " + teacherId + " ha aceptado la notificación del pago ID: " + paymentId;
            System.out.println(confirmationMessage);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Notificación aceptada", confirmationMessage);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al aceptar la notificación", null);
        }
    }
}
