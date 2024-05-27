package BP.domain.dao;

import BP.domain.entity.Guardian;
import org.springframework.stereotype.Repository;

@Repository
public interface IGuardianRepo extends IGenericRepo<Guardian, Integer> {
}
