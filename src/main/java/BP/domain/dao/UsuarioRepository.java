package BP.domain.dao;


import BP.domain.entity.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
}
