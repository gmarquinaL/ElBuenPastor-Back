package BP.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Children
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, length = 30)
    private String displayName;

    @Column(nullable = false, length = 30)
    private String iconName;

    @Column(length = 30)
    private String bgcolor;

    @Column(nullable = false, length = 30)
    private String route;

    @ManyToOne()
    @JoinColumn(name = "id_menu", nullable = false, foreignKey = @ForeignKey(name = "FK_CHILDREN_MENU"))
    private Menu menu;
}
