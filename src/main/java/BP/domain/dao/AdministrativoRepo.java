package BP.domain.dao;


import BP.domain.entity.Administrative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrativoRepo extends JpaRepository<Administrative, Integer> {
    // Este repositorio gestionará las funcionalidades administrativas básicas.
}
