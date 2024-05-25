package BP.application.service.impl;

import BP.application.exception.ModelNotFoundException;
import BP.application.util.ConstantUtil;
import BP.domain.dao.IGenericRepo;
import BP.domain.dao.IUserRepo;
import BP.domain.entity.User;
import BP.application.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User, Integer> implements IUserService
{

    private final IUserRepo repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected IGenericRepo<User, Integer> getRepo() {
        return repo;
    }

    @Transactional
    @Override
    public User save(User user) throws Exception {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @Transactional
    @Override
    public User update(User user, Integer integer) throws Exception {
        User userFound = repo.findById(integer).orElseThrow(() -> new ModelNotFoundException(ConstantUtil.RESOURCE_NOT_FOUND + integer));
        userFound.setUsername(user.getUsername());

        userFound.setPassword(passwordChangeRequired(user, user.getPassword()) != null ? user.getPassword() : userFound.getPassword());

        return super.update(userFound, integer);
    }

    @Override
    public User findOneByUsername(String username) {
        return repo.findOneByUsername(username);
    }

    private String passwordChangeRequired(User user, String value)
    {
        if(value != null && !value.isEmpty())
        {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return user.getPassword();
        }
        return null;
    }
}
