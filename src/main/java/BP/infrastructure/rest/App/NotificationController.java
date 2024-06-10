package BP.infrastructure.rest.App;

import BP.application.service.impl.App.NotificationService;
import BP.application.service.impl.App.PagosService;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.entity.App.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PagosService pagosService;


    // Enviar notificación de pago a un docente específico
    @PostMapping("/enviar/{paymentId}")
    public ResponseEntity<BestGenericResponse<String>> enviarNotificacionPago(@PathVariable int paymentId) {
        BestGenericResponse<String> response = notificationService.enviarNotificacionPago(paymentId);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

    // Aceptar notificación de pago por parte del docente
    @PostMapping("/aceptar/{teacherId}/{paymentId}/{notificationId}")
    public ResponseEntity<BestGenericResponse<String>> aceptarNotificacionPago(@PathVariable int teacherId, @PathVariable int paymentId, @PathVariable int notificationId) {
        BestGenericResponse<String> response = pagosService.aceptarNotificacionPago(teacherId, paymentId);
        if (response.getRpta() == Global.RPTA_OK) {
            return ResponseEntity.status(200).body(notificationService.marcarNotificacionComoLeida(notificationId));
        } else {
            return ResponseEntity.status(400).body(response);
        }
    }
    @GetMapping("/listar/{teacherId}")
    public ResponseEntity<BestGenericResponse<List<Notification>>> listarNotificacionesPorDocente(@PathVariable int teacherId) {
        BestGenericResponse<List<Notification>> response = notificationService.listarNotificacionesPorDocente(teacherId);
        return ResponseEntity.status(response.getRpta() == Global.RPTA_OK ? 200 : 400).body(response);
    }

}
