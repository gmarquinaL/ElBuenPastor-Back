package BP.domain.dao;

import BP.domain.entity.StudentSiblings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSiblingsRepo extends JpaRepository<StudentSiblings, Integer> {
}