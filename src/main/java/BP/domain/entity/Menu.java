package BP.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Menu
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(length = 30, nullable = false)
    private String displayName;

    @Column(length = 30, nullable = false)
    private String iconName;

    @Column(length = 30)
    private String bgcolor;

    @Column(length = 30, nullable = false)
    private String route;

}
