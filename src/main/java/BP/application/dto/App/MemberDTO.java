package BP.application.dto.App;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private int id;
    private String email;
    private String password; // Añadir campo de contraseña
    private boolean validity;
    private TeacherDTO teacher;

    // Constructor con contraseña
    public MemberDTO(int id, String email, String password, boolean validity, TeacherDTO teacher) {
        this.id = id;
        this.email = email;
        this.password = password; // Añadir contraseña al constructor
        this.validity = validity;
        this.teacher = teacher;
    }

    // Constructor vacío
    public MemberDTO() {
    }


}
