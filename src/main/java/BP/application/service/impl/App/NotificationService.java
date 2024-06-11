package BP.application.service.impl.App;

import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.App.NotificationRepository;
import BP.domain.entity.App.Notification;
import BP.domain.entity.App.Teacher;
import BP.domain.entity.App.TeacherPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private PagosService pagosService;  // Cambio para usar PagosService
    @Autowired
    private NotificationRepository notificationRepository;
    // Enviar notificación de pago a un docente específico
// Enviar notificación de pago a un docente específico
    public BestGenericResponse<String> enviarNotificacionPago(int paymentId) {
        BestGenericResponse<TeacherPayment> paymentResponse = pagosService.obtenerPagoPorId(paymentId);
        if (paymentResponse.getRpta() == Global.RPTA_OK && paymentResponse.getBody().getPaymentStatus().equals("Pendiente")) {
            TeacherPayment payment = paymentResponse.getBody();
            Teacher teacher = payment.getTeacher();
            String message = "Estimado/a " + teacher.getFullName() + ",Tiene un Nuevo Pago Programado" + payment.getAmount() + "Para la Fecha " + payment.getPaymentDate() + ".";

            // Verificar si ya existe una notificación para este pago
            Optional<Notification> existingNotification = notificationRepository.findByTeacherAndMessage(teacher, message);
            if (existingNotification.isPresent()) {
                return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Ya ha enviado una notificación con anterioridad a este docente", null);
            }

            Notification notification = new Notification(teacher, message, LocalDateTime.now(), false);
            notificationRepository.save(notification);
            System.out.println("Notificación enviada a: " + teacher.getEmail() + " Mensaje: " + message);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Notificación enviada correctamente", message);
        } else {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Pago no está pendiente o no encontrado", null);
        }
    }
    // Listar notificaciones de un docente específico que no han sido leídas
    public BestGenericResponse<List<Notification>> listarNotificacionesPorDocente(int teacherId) {
        try {
            List<Notification> notifications = notificationRepository.findByTeacherIdAndIsReadFalseOrderBySentAtDesc(teacherId);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Notificaciones obtenidas correctamente", notifications);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener las notificaciones", null);
        }
    }

    // Marcar notificación como leída cuando es aceptada
    public BestGenericResponse<String> marcarNotificacionComoLeida(int notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            notificationRepository.save(notification);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Notificación marcada como leída", "Notificación actualizada");
        } else {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Notificación no encontrada", null);
        }
    }
}
