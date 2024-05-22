package BP.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuDTO
{
    private Integer id;

    @NotBlank(message = "El nombre del menu es obligatorio")
    @Length(min = 3, max = 30, message = "El nombre del menu debe tener entre 3 y 30 caracteres")
    private String displayName;

    @NotBlank(message = "El nombre del icono es obligatorio")
    @Length(min = 3, max = 30, message = "El nombre del icono debe tener entre 3 y 30 caracteres")
    private String iconName;

    @Length(min = 3, max = 30, message = "El color de fondo debe tener entre 3 y 30 caracteres")
    private String bgcolor;

    @NotBlank(message = "La ruta es obligatoria")
    @Length(min = 3, max = 30, message = "La ruta debe tener entre 3 y 30 caracteres")
    private String route;

    @JsonManagedReference
    private List<ChildrenDTO> children;
}
