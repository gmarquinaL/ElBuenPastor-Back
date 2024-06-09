package BP.domain.dao.App;

import BP.domain.entity.App.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByTeacherIdOrderBySentAtDesc(int teacherId);
}
