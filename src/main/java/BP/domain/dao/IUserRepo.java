package BP.domain.dao;

import BP.domain.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepo extends IGenericRepo<User, Integer> {
    User findOneByUsername(String username);
}
