package BP.domain.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer extends Person
{

    private Integer creditLimit;

    private Float discount;
}
