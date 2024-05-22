package BP.domain.dao;

import BP.domain.entity.Menu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMenuRepo extends IGenericRepo<Menu, Integer> {
    @Query(value = "SELECT * FROM menu", nativeQuery = true)
    List<Menu> getAllMenus();
}
