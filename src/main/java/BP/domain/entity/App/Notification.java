package BP.domain.entity.App;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private String message;

    private LocalDateTime sentAt;

    private boolean isRead;

    public Notification() {
    }

    public Notification(Teacher teacher, String message, LocalDateTime sentAt, boolean isRead) {
        this.teacher = teacher;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }


}
