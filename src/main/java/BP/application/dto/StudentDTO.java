package BP.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {
    private Integer id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Length(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    private String fullName;

    @NotBlank(message = "El nivel es obligatorio")
    @Length(min = 1, max = 50, message = "El nivel debe tener entre 1 y 50 caracteres")
    private String level;

    @NotBlank(message = "La sección es obligatoria")
    @Length(min = 1, max = 50, message = "La sección debe tener entre 1 y 50 caracteres")
    private String section;

    @NotBlank(message = "El grado es obligatorio")
    @Length(min = 1, max = 50, message = "El grado debe tener entre 1 y 50 caracteres")
    private String grade;

    private Boolean current;

    private String  gender;

    private GuardianDTO guardian;
    private List<StudentDTO> siblings;  // Lista de hermanos, también representados como StudentDTO

    public boolean isCurrent() {
        return current;
    }

    public String getGender() {
        return gender;
    }
}
