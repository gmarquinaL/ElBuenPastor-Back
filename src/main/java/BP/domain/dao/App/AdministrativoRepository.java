package BP.domain.dao.App;


import BP.domain.entity.App.Administrative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrativoRepository extends JpaRepository<Administrative, Integer> {
    // Este repositorio gestionará las funcionalidades administrativas básicas.
}
