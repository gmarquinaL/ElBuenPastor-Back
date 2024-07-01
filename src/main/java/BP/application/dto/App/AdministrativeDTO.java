package BP.application.dto.App;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class AdministrativeDTO {
    private int id;
    private String fullName;
    private String position;
    private String dni;
    private String email;
    private String phone;
    private String address;
    private LocalDate hiringDate;
    private boolean active;

    public AdministrativeDTO() {
    }
}
