package BP.domain.dao.App;


import BP.domain.entity.App.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepository extends CrudRepository<Location, Integer> {
}
