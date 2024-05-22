package BP.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public class Person
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    protected Integer id;

    @Column(length = 50, nullable = false)
    protected String name;

    @Column(length = 30, nullable = false)
    protected String paternalSurname;

    @Column(length = 30, nullable = false)
    protected String maternalSurname;

    @Column(nullable = false)
    protected LocalDate birthdate;

    @Column(length = 11, unique = true)
    protected String ruc;

    @Column(nullable = false, length = 120)
    protected String address;

    @Column(length = 9)
    protected String phone;

    @Column(nullable = false, length = 150)
    protected String email;

    protected String gender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user", unique = true, nullable = false, foreignKey = @ForeignKey(name = "FK_PERSON_USER"))
    protected User user;

}
