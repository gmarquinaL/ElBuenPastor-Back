package BP.application.dto.App;

import BP.domain.entity.App.Teacher;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private int id;
    private String email;
    private boolean validity;
    private TeacherDTO teacher;

    // Constructor
    public MemberDTO(int id, String email, boolean validity, TeacherDTO teacher) {
        this.id = id;
        this.email = email;
        this.validity = validity;
        this.teacher = teacher;
    }

    // Constructor vacío
    public MemberDTO() {
    }
}