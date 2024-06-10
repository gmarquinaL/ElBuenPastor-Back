package BP.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 50)
    private String level;

    @Column(nullable = false, length = 50)
    private String section;

    @Column(nullable = false, length = 50)
    private String grade;

    @Column(nullable = false)
    private boolean current;

    private String gender;

    @ManyToOne
    @JoinColumn(name = "guardian_id")
    @JsonBackReference
    private Guardian guardian;

    // Método para obtener hermanos a través del apoderado común
    public List<Student> getSiblings() {
        if (guardian == null) {
            return new ArrayList<>();
        }
        return guardian.getStudents().stream()
                .filter(s -> !s.getId().equals(this.getId()))
                .collect(Collectors.toList());
    }
}
