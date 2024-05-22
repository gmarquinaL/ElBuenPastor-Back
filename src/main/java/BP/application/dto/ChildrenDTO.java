package BP.application.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChildrenDTO
{
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Length(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres")
    private String displayName;

    @NotBlank(message = "El icono es obligatorio")
    @Length(min = 3, max = 30, message = "El icono debe tener entre 3 y 30 caracteres")
    private String iconName;

    @Length(min = 3, max = 30, message = "El color debe tener entre 3 y 30 caracteres")
    private String bgcolor;

    @Length(min = 3, max = 30, message = "El color debe tener entre 3 y 30 caracteres")
    private String route;

    @JsonBackReference
    private MenuDTO menu;
}
