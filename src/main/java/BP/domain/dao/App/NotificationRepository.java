package BP.domain.dao.App;

import BP.domain.entity.App.Notification;
import BP.domain.entity.App.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByTeacherIdAndIsReadFalseOrderBySentAtDesc(int teacherId);
    Optional<Notification> findByTeacherAndMessage(Teacher teacher, String message);
}
