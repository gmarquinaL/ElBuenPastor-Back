package BP.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuardianDTO {
    private Integer id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Length(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    private String fullName;

    private Boolean livesWithStudent;

    public boolean isLivesWithStudent() {
        return livesWithStudent;
    }
}
