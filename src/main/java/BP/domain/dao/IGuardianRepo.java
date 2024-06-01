package BP.domain.dao;

import BP.domain.entity.Guardian;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGuardianRepo extends IGenericRepo<Guardian, Integer> {

    @Query("SELECT g FROM Guardian g WHERE LOWER(g.fullName) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Guardian> searchByName(@Param("name") String name);

}
