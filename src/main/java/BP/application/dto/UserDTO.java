package BP.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO
{
    private Integer id;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Length(min = 3, max = 120, message = "El nombre de usuario debe tener entre 3 y 120 caracteres")
    private String username;

    @Length(min = 3, max = 60, message = "La contraseña debe tener entre 3 y 60 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
