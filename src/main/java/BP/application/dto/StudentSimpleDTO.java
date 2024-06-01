package BP.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSimpleDTO {
    private Integer id;
    private String fullName; // Nombre completo del estudiante
    private String guardianName; // Nombre del apoderado
    private String siblingStatus; // Indicador de si tiene hermanos o no

    // Constructor para cuando no tiene hermano
    public StudentSimpleDTO(Integer id, String fullName, String guardianName) {
        this.id = id;
        this.fullName = fullName;
        this.guardianName = guardianName;
        this.siblingStatus = "No tiene hermanos";
    }
}
