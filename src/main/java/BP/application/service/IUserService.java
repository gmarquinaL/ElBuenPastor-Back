package BP.application.service;

import BP.domain.entity.User;

public interface IUserService extends ICRUD<User, Integer> {
    User findOneByUsername(String username);
}
