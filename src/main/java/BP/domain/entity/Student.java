package BP.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private Guardian guardian;

    @ManyToMany
    @JoinTable(
            name = "student_siblings",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "sibling_id")
    )
    private List<Student> siblings = new ArrayList<>();

    public void setSiblings(List<Student> siblings) {
        this.siblings = siblings;
    }

    public void addSibling(Student sibling) {
        if (this.siblings == null) {
            this.siblings = new ArrayList<>();
        }
        this.siblings.add(sibling);
    }
}
