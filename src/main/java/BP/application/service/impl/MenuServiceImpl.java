package BP.application.service.impl;

import BP.domain.dao.IGenericRepo;
import BP.domain.dao.IMenuRepo;
import BP.domain.entity.Menu;
import BP.application.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu, Integer> implements IMenuService {

    private final IMenuRepo repo;

    @Override
    protected IGenericRepo<Menu, Integer> getRepo() {
        return repo;
    }

    @Override
    public List<Menu> getMenusByUsername() {
        // Ya no necesitamos usar el nombre de usuario, simplemente obtenemos todos los menús
        return repo.getAllMenus();
    }
}
