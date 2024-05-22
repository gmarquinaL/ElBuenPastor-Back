package BP.application.service;

import BP.domain.entity.Menu;

import java.util.List;

public interface IMenuService extends ICRUD<Menu, Integer>
{
    List<Menu> getMenusByUsername();
}
