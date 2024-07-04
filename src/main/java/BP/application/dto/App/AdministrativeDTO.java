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

    public AdministrativeDTO(int id) {
        this.id = id;
    }

    public AdministrativeDTO(int id, String fullName, String position, String dni, String email, String phone, String address, LocalDate hiringDate, boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.dni = dni;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.hiringDate = hiringDate;
        this.active = active;
    }
}
