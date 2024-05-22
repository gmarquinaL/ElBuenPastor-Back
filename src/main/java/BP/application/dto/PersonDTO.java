package BP.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDTO
{
    protected Integer id;

    @NotBlank(message = "El nombre es requerido")
    @Length(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    protected String name;

    @NotBlank(message = "El apellido paterno es requerido")
    @Length(min = 3, max = 30, message = "El apellido paterno debe tener entre 3 y 30 caracteres")
    protected String paternalSurname;

    @NotBlank(message = "El apellido materno es requerido")
    @Length(min = 3, max = 30, message = "El apellido materno debe tener entre 3 y 30 caracteres")
    protected String maternalSurname;

    @NotNull(message = "La fecha de nacimiento es requerida")
    protected LocalDate birthdate;

    @Length(min = 11, max = 11, message = "El RUC debe tener 11 caracteres")
    protected String ruc;

    @NotBlank(message = "La dirección es requerido")
    @Length(min = 6, max = 120, message = "La dirección debe tener entre 6 y 120 caracteres")
    protected String address;

    @Length(min = 9, max = 9, message = "El teléfono debe tener 9 caracteres")
    protected String phone;

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo electrónico es requerido")
    @Length(min = 6, max = 150, message = "El correo electrónico debe tener entre 6 y 150 caracteres")
    protected String email;


    @NotNull(message = "El usuario es requerido")
    protected UserDTO user;
}
