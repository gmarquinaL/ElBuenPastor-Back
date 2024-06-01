package BP.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SiblingRelationship> siblingRelationships = new ArrayList<>();

    @OneToMany(mappedBy = "sibling", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SiblingRelationship> reverseSiblingRelationships = new ArrayList<>();
}
